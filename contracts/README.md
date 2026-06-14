# contracts/

Shared, transport-independent **contract** between the backend and clients (Godot first).
The message contract matters more than the transport — see
[docs/BUILD_PROMPT.md](../docs/BUILD_PROMPT.md) principle #3.

Contents (**v1**, documents the live backend):

- [`rest/endpoints.md`](rest/endpoints.md) — REST contract: auth, characters, world/timeline,
  narrative (solo + co-op), party. Request/response shapes, auth, status codes.
- [`ws/protocol.md`](ws/protocol.md) — WebSocket protocol: the transport-independent
  `{type,data}` envelope, commands, events, error codes, an example session.
- [`VERSIONS.md`](VERSIONS.md) — protocol version history and compatibility rules.

> The backend is the source of truth; clients send intentions, the backend validates and decides.
> These docs are the hand-authored source of truth for v1; machine-readable OpenAPI/JSON-Schema
> generation can layer on later without changing the contract.
