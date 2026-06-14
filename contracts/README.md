# contracts/

Shared, transport-independent **contract** between the backend and clients (Godot first).
The message contract matters more than the transport — see
[docs/BUILD_PROMPT.md](../docs/BUILD_PROMPT.md) principle #3.

Planned contents (filled in during **E11 — Contracts & Versioning**):

- `rest/` — OpenAPI specs for REST/HTTP-2 endpoints (auth, character, profile, admin).
- `ws/` — JSON Schemas for WebSocket **commands** (client→server intentions) and **events**
  (server→client). Transport-independent so we can later move to UDP/ENet/protobuf/FlatBuffers
  without rewriting game logic.
- `examples/` — concrete example messages per command/event.
- `VERSIONS.md` — protocol version history and compatibility rules.

> Empty-but-structured for now. The backend is the source of truth; clients send intentions,
> the backend validates and decides.
