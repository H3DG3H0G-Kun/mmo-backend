using System;
using System.Collections.Concurrent;
using System.Net.WebSockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using UnityEngine;

namespace Watcher.Net
{
    /// <summary>
    /// WebSocket session against /ws. Uses System.Net.WebSockets (desktop/standalone). Incoming
    /// frames are queued on a background task and dispatched on the Unity main thread via
    /// <see cref="Poll"/>, so event handlers may safely touch Unity objects. For a WebGL build,
    /// swap this implementation for the NativeWebSocket package (same public surface).
    /// </summary>
    public class GameSocket
    {
        private ClientWebSocket _ws;
        private readonly ConcurrentQueue<string> _incoming = new();
        private readonly SemaphoreSlim _sendLock = new(1, 1);
        private CancellationTokenSource _cts;

        // Typed events (raised on the main thread from Poll()).
        public event Action OnSessionWelcome;
        public event Action OnWorldEntered;
        public event Action<EntityDto[]> OnSnapshot;
        public event Action<EntityDto> OnEntityJoined;
        public event Action<string, float, float, float> OnEntityMoved;
        public event Action<string> OnEntityLeft;
        public event Action<string, string> OnError;

        public bool IsOpen => _ws != null && _ws.State == WebSocketState.Open;

        public async Task ConnectAsync(string url)
        {
            _cts = new CancellationTokenSource();
            _ws = new ClientWebSocket();
            await _ws.ConnectAsync(new Uri(url), _cts.Token);
            _ = Task.Run(ReceiveLoop);
        }

        public void SendHello(string token) => Send(Protocol.CmdSessionHello, new { token });
        public void SendEnterWorld(string characterId) => Send(Protocol.CmdEnterWorld, new { characterId });
        public void SendMove(float x, float y, float z) => Send(Protocol.CmdMove, new { x, y, z });
        public void SendPing() => Send(Protocol.CmdPing, new { });

        /// <summary>Call every frame (e.g. in MonoBehaviour.Update) to dispatch queued events.</summary>
        public void Poll()
        {
            while (_incoming.TryDequeue(out var raw))
            {
                try { Dispatch(raw); }
                catch (Exception e) { Debug.LogError("[GameSocket] dispatch error: " + e); }
            }
        }

        public async Task CloseAsync()
        {
            try { _cts?.Cancel(); } catch { /* ignore */ }
            if (_ws is { State: WebSocketState.Open })
                await _ws.CloseAsync(WebSocketCloseStatus.NormalClosure, "bye", CancellationToken.None);
        }

        // --- internals ---

        private void Dispatch(string raw)
        {
            var obj = JObject.Parse(raw);
            var type = (string)obj["type"];
            var data = obj["data"];
            switch (type)
            {
                case Protocol.EvtSessionWelcome: OnSessionWelcome?.Invoke(); break;
                case Protocol.EvtWorldEntered: OnWorldEntered?.Invoke(); break;
                case Protocol.EvtWorldSnapshot:
                    var list = data?["entities"]?.ToObject<EntityDto[]>() ?? Array.Empty<EntityDto>();
                    OnSnapshot?.Invoke(list);
                    break;
                case Protocol.EvtEntityJoined: OnEntityJoined?.Invoke(data!.ToObject<EntityDto>()); break;
                case Protocol.EvtEntityMoved:
                    OnEntityMoved?.Invoke((string)data!["characterId"],
                        (float)data["x"], (float)data["y"], (float)data["z"]);
                    break;
                case Protocol.EvtEntityLeft: OnEntityLeft?.Invoke((string)data!["characterId"]); break;
                case Protocol.EvtError: OnError?.Invoke((string)data!["code"], (string)data["message"]); break;
            }
        }

        private async void Send(string type, object data)
        {
            if (!IsOpen) return;
            var json = JsonConvert.SerializeObject(new { type, data });
            var bytes = Encoding.UTF8.GetBytes(json);
            await _sendLock.WaitAsync();
            try { await _ws.SendAsync(new ArraySegment<byte>(bytes), WebSocketMessageType.Text, true, _cts.Token); }
            catch (Exception e) { Debug.LogWarning("[GameSocket] send failed: " + e.Message); }
            finally { _sendLock.Release(); }
        }

        private async Task ReceiveLoop()
        {
            var buffer = new byte[8192];
            var sb = new StringBuilder();
            try
            {
                while (_ws.State == WebSocketState.Open && !_cts.IsCancellationRequested)
                {
                    var result = await _ws.ReceiveAsync(new ArraySegment<byte>(buffer), _cts.Token);
                    if (result.MessageType == WebSocketMessageType.Close) break;
                    sb.Append(Encoding.UTF8.GetString(buffer, 0, result.Count));
                    if (result.EndOfMessage)
                    {
                        _incoming.Enqueue(sb.ToString());
                        sb.Clear();
                    }
                }
            }
            catch (OperationCanceledException) { /* closing */ }
            catch (Exception e) { Debug.LogWarning("[GameSocket] receive loop ended: " + e.Message); }
        }
    }
}
