# clients/godot-client/

The visual client for the **Watcher** MMO. Target engine: **Godot 4.5.1 stable**.

> Intentionally empty for now. The backend contract comes first — Godot work begins at
> **E10 — Godot Client v1**, after the WebSocket/REST contract is stable
> (see [docs/BUILD_PROMPT.md](../../docs/BUILD_PROMPT.md)).

When work starts, this folder holds the Godot project (`project.godot`, scenes, scripts) and
consumes the shared schemas from [`../../contracts/`](../../contracts). The client only sends
*intentions* (move, attack, enter Thread, accept tale, claim reward); the backend validates and
decides all authoritative state.
