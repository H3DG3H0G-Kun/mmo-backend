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
| `WORLD_SNAPSHOT` | `{ "entities": [Entity] }` | Everyone already in your era — other Watchers (`kind:"PLAYER"`) **and** server NPCs (`kind:"NPC"`, with a `role`). Sent right after `WORLD_ENTERED`. |
| `ENTITY_JOINED` | `Entity` (`kind:"PLAYER"`) | Another Watcher entered your era. |
| `ENTITY_MOVED` | `{ "characterId", "x", "y", "z" }` | Another Watcher moved. |
| `ENTITY_LEFT` | `{ "characterId" }` | Another Watcher left your era (disconnected). |
| `PONG` | `{}` | Reply to `PING`. |
| `ERROR` | `{ "code", "message" }` | A command was rejected. |

### Error codes
`BAD_MESSAGE` (malformed/missing type) · `UNAUTHENTICATED` (command before `SESSION_HELLO`) ·
`INVALID_TOKEN` (bad/expired/missing token) · `NOT_FOUND` (e.g. character not yours) ·
`NOT_IN_WORLD` (`MOVE` before `ENTER_WORLD`) · `UNKNOWN_TYPE` (unrecognized command).

### Entity shape
```
Entity = { characterId, name, x, y, z, kind }   // kind ∈ "PLAYER" | "NPC"
          // NPC entities also carry: role ∈ "VENDOR" | "HERALD" | "TOWNSFOLK" | "GUARD"
          // NPC characterId is the npc code (e.g. "npc_pharna"); NPCs are static (no ENTITY_MOVED).
```
Interact with a `VENDOR` NPC by opening the market (`GET /api/economy/market`). NPCs in an era are
also listable over REST: `GET /api/world/npcs?characterId=<uuid>` → `[{ code, name, role, locationCode, x, y }]`.

> The era is the **area-of-interest** unit for v1: snapshots and entity events are scoped to the
> Watcher's current era. Player presence is in-memory/ephemeral (rebuilt on reconnect); NPCs are
> server-defined data, included in each joining Watcher's snapshot.

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
