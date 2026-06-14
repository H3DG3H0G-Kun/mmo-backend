using System.Collections.Generic;
using UnityEngine;
using Watcher.Net;
using Watcher.World;

namespace Watcher
{
    /// <summary>
    /// Wires the whole "prove the pipe" flow: register-or-login → ensure a character → open the
    /// WebSocket → enter the world. Attach to an empty GameObject and fill the inspector fields.
    /// </summary>
    public class GameBootstrap : MonoBehaviour
    {
        [Header("Endpoints (use :18080/:18090 when running services locally)")]
        public string authUrl = "http://localhost:8080";
        public string worldUrl = "http://localhost:8090";
        public string wsUrl = "ws://localhost:8090/ws";

        [Header("Account (created on first run if absent)")]
        public string username = "watcher1";
        public string password = "supersecret";
        public string characterName = "Mtsveli";

        [Tooltip("Auto-added if left empty.")]
        public WorldManager world;

        private GameSocket _socket;

        private async void Start()
        {
            if (world == null) world = gameObject.AddComponent<WorldManager>();
            try
            {
                var api = new ApiClient(authUrl, worldUrl);

                Debug.Log("[boot] register-or-login…");
                var token = await api.RegisterOrLoginAsync(username, password);

                Debug.Log("[boot] ensuring a character…");
                List<CharacterDto> chars = await api.ListCharactersAsync();
                CharacterDto ch = chars.Count > 0 ? chars[0] : await api.CreateCharacterAsync(characterName);
                Debug.Log($"[boot] character {ch.name} in era {ch.currentEra?.code}");

                _socket = new GameSocket();
                world.Init(_socket, ch.id, ch.name);

                Debug.Log("[boot] connecting WebSocket…");
                await _socket.ConnectAsync(wsUrl);
                _socket.SendHello(token.accessToken);
                _socket.SendEnterWorld(ch.id);
                Debug.Log("[boot] in the world — WASD to move; other Watchers appear as capsules.");
            }
            catch (System.Exception e)
            {
                Debug.LogError("[boot] failed: " + e.Message);
            }
        }

        private async void OnDestroy()
        {
            if (_socket != null) await _socket.CloseAsync();
        }
    }
}
