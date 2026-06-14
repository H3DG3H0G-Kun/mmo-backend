# WebSocket protocol v1

Realtime channel for session + world. Endpoint: `ws://<world-service>/ws` (local: `:8090`).
Every message is a JSON **envelope**:

```json
{ "type": "<NAME>", "data": { ... } }
```

The envelope is **transport-independent** by design — today it rides WebSocket/JSON; it can later
ride UDP/ENet/protobuf without changing game logic. The HTTP handshake itself is unauthenticated;
the client authenticates by sending `SESSION_HELLO` as its first message.

## Commands (client → server, intentions)

| type | data | meaning |
|------|------|---------|
| `SESSION_HELLO` | `{ "token": "<jwt>" }` | Authenticate this connection. Must be sent first. |
| `ENTER_WORLD` | `{ "characterId": "<uuid>" }` | Enter the world as a character you own. |
| `MOVE` | `{ "x": <num>, "y": <num>, "z": <num> }` | Move; broadcast to others in your era. Requires being in the world. |
| `PING` | `{}` | Liveness check. |

## Events (server → client, decisions/state)

| type | data | meaning |
|------|------|---------|
| `SESSION_WELCOME` | `{ "accountId", "username" }` | Token accepted; session authenticated. |
| `WORLD_ENTERED` | `{ "characterId", "name", "era": { "id", "code", "name" } }` | Spawned into the world. |
| `WORLD_SNAPSHOT` | `{ "entities": [{ "characterId", "name", "x", "y", "z" }] }` | The other Watchers already in your era (sent right after `WORLD_ENTERED`). |
| `ENTITY_JOINED` | `{ "characterId", "name", "x", "y", "z" }` | Another Watcher entered your era. |
| `ENTITY_MOVED` | `{ "characterId", "x", "y", "z" }` | Another Watcher moved. |
| `ENTITY_LEFT` | `{ "characterId" }` | Another Watcher left your era (disconnected). |
| `PONG` | `{}` | Reply to `PING`. |
| `ERROR` | `{ "code", "message" }` | A command was rejected. |

### Error codes
`BAD_MESSAGE` (malformed/missing type) · `UNAUTHENTICATED` (command before `SESSION_HELLO`) ·
`INVALID_TOKEN` (bad/expired/missing token) · `NOT_FOUND` (e.g. character not yours) ·
`NOT_IN_WORLD` (`MOVE` before `ENTER_WORLD`) · `UNKNOWN_TYPE` (unrecognized command).

> The era is the **area-of-interest** unit for v1: snapshots and entity events are scoped to the
> Watcher's current era. Presence is in-memory and ephemeral (rebuilt on reconnect).

## Example session

```
→ { "type": "SESSION_HELLO", "data": { "token": "eyJ..." } }
← { "type": "SESSION_WELCOME", "data": { "accountId": "...", "username": "watcher" } }
→ { "type": "ENTER_WORLD", "data": { "characterId": "..." } }
← { "type": "WORLD_ENTERED", "data": { "characterId": "...", "name": "Mtsveli",
                                       "era": { "id": 1, "code": "PARNAVAZ", "name": "The Age of Parnavaz" } } }
→ { "type": "PING" }
← { "type": "PONG", "data": {} }
```

## Notes / roadmap
- v1 carries session + world entry. Movement, world snapshots, party updates, instance/beat
  events, and reward events will extend this **same envelope** (new `type`s, no protocol break).
- Authentication is per-connection; the same JWT is reused from the REST auth flow.
