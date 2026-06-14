using System.Collections.Generic;
using UnityEngine;
using Watcher.Net;

namespace Watcher.World
{
    /// <summary>
    /// Renders the shared world from server presence events and drives the local Watcher.
    /// Backend (x, y) is the ground plane → Unity (x, z); Unity y stays 0 (flat world for v1).
    /// </summary>
    public class WorldManager : MonoBehaviour
    {
        [Tooltip("Local movement speed (units/sec).")]
        public float MoveSpeed = 5f;

        [Tooltip("How often to send MOVE to the server (seconds).")]
        public float MoveSendInterval = 0.1f;

        private GameSocket _socket;
        private string _localId;
        private GameObject _localPlayer;
        private readonly Dictionary<string, RemoteWatcher> _others = new();
        private float _moveTimer;
        private Vector3 _lastSentPos;

        public void Init(GameSocket socket, string localCharacterId, string localName)
        {
            _socket = socket;
            _localId = localCharacterId;

            _localPlayer = MakeCapsule("You:" + localName, new Color(0.78f, 0.65f, 0.38f)); // gold
            _localPlayer.transform.position = Vector3.zero;
            _lastSentPos = Vector3.zero;

            socket.OnSnapshot += OnSnapshot;
            socket.OnEntityJoined += OnEntityJoined;
            socket.OnEntityMoved += OnEntityMoved;
            socket.OnEntityLeft += OnEntityLeft;
            socket.OnError += (code, msg) => Debug.LogWarning($"[world] server error {code}: {msg}");
        }

        private void Update()
        {
            _socket?.Poll();
            if (_localPlayer == null) return;

            // Local movement (old Input Manager — set Active Input Handling to "Both" or "Old").
            float h = Input.GetAxisRaw("Horizontal");
            float v = Input.GetAxisRaw("Vertical");
            var move = new Vector3(h, 0f, v);
            if (move.sqrMagnitude > 0.001f)
                _localPlayer.transform.position += move.normalized * (MoveSpeed * Time.deltaTime);

            _moveTimer += Time.deltaTime;
            if (_moveTimer >= MoveSendInterval)
            {
                _moveTimer = 0f;
                var p = _localPlayer.transform.position;
                if ((p - _lastSentPos).sqrMagnitude > 0.0004f)
                {
                    _lastSentPos = p;
                    _socket?.SendMove(p.x, p.z, 0f); // backend (x,y) <- Unity (x,z)
                }
            }
        }

        private void OnSnapshot(EntityDto[] entities)
        {
            foreach (var e in entities) Upsert(e);
        }

        private void OnEntityJoined(EntityDto e) => Upsert(e);

        private void OnEntityMoved(string characterId, float x, float y, float z)
        {
            if (characterId == _localId) return;
            if (_others.TryGetValue(characterId, out var w)) w.SetTarget(ToWorld(x, y));
        }

        private void OnEntityLeft(string characterId)
        {
            if (_others.TryGetValue(characterId, out var w))
            {
                Destroy(w.gameObject);
                _others.Remove(characterId);
            }
        }

        private void Upsert(EntityDto e)
        {
            if (e.characterId == _localId) return; // never render ourselves as a remote
            if (!_others.TryGetValue(e.characterId, out var w))
            {
                var go = MakeCapsule("Watcher:" + e.name, new Color(0.48f, 0.42f, 1f)); // accent
                w = go.AddComponent<RemoteWatcher>();
                _others[e.characterId] = w;
            }
            var pos = ToWorld(e.x, e.y);
            w.transform.position = pos;
            w.SetTarget(pos);
        }

        private static Vector3 ToWorld(float backendX, float backendY) => new(backendX, 0f, backendY);

        private static GameObject MakeCapsule(string name, Color color)
        {
            var go = GameObject.CreatePrimitive(PrimitiveType.Capsule);
            go.name = name;
            var r = go.GetComponent<Renderer>();
            if (r != null) r.material.color = color;
            return go;
        }
    }
}
