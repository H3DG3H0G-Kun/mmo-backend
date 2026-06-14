# Protocol versions

## v1 (current)
- **REST** — see [rest/endpoints.md](rest/endpoints.md): auth (register/login/me), characters,
  world/timeline (eras, travel, living timeline), narrative (solo + co-op), party.
- **WebSocket** — see [ws/protocol.md](ws/protocol.md): JSON envelope `{type,data}`;
  `SESSION_HELLO`/`ENTER_WORLD`/`PING` commands; `SESSION_WELCOME`/`WORLD_ENTERED`/`PONG`/`ERROR` events.

### Compatibility rules
- Additive changes (new endpoints, new WS `type`s, new optional fields) are **backward compatible**
  and do **not** bump the major version.
- Removing/renaming fields or endpoints, or changing a field's type/meaning, is a **breaking**
  change and bumps the version (`v2`), kept side-by-side during migration.
- Clients must ignore unknown WS `type`s and unknown JSON fields.
