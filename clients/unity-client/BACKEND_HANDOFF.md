# Backend → Client handoff (Phase 1 content + Phase 2 world geography)

What the Unity/client session must render. Full shapes in [contracts/](../../contracts).
Backend runs locally on auth `:18080`, world `:18090`, ws `:18090/ws` (`scripts\windows\run-backend.bat`).

## Phase 1 — narrative (small client change)
- **CHOOSE labels.** `TaleStateView.currentBeat.choices` is now `[{ key, label }]`. Render the
  `label` on choice buttons (the server returns an authored label, or a humanized key as fallback)
  and send the `key` back to `/advance`.
- 28 authored tales now live across eras 1–13. Resonance / enter / advance shapes are unchanged.

## Phase 2 — world geography (NEW — needs UI)

### REST (world-service)
- `GET /api/world/locations?characterId=<uuid>` →
  `{ eraCode, currentLocationCode|null, locations: [{ code, name, type, region, x, y, description, connected }] }`
  - `type` ∈ `TOWN | LANDMARK | SACRED | PLACE_OF_POWER | WILDS` — use `TOWN` for the "inTown" ambient.
  - `connected` = reachable from where the character stands (all true when freshly arrived in an era).
- `GET /api/world/locations/era/{eraCode}` → same shape (no character; browse/maps).
- `POST /api/world/travel-to` `{ characterId, locationCode }` → refreshed locations response.
  `409` not connected · `404` unknown location in era. **Intra-era only** (crossing eras stays
  `POST /api/world/travel`).
- `GET /api/world/npcs?characterId=<uuid>` → `[{ code, name, role, locationCode, x, y }]`.

> **Key link:** a location's `code` is the `place` you pass to `/api/narrative/resonances` and
> `/api/narrative/tales/{code}/enter`. Flow: travel to a location → use `currentLocationCode` as
> `place` → the tales that resonate there appear.

### WebSocket (`/ws`)
- `WORLD_SNAPSHOT.entities` now contains **both players and NPCs**. Each entity:
  `{ characterId, name, x, y, z, kind }`, `kind` ∈ `PLAYER | NPC`. NPC entities also carry
  `role` ∈ `VENDOR | HERALD | TOWNSFOLK | GUARD`; their `characterId` is the npc code.
- `ENTITY_JOINED` is a `PLAYER`. NPCs are static (no `ENTITY_MOVED` for them).
- Render NPCs distinctly (role tint/label) and allow proximity-interaction. A `VENDOR` opens the
  market: `GET /api/economy/market` (existing economy API).

## Client to-do
1. Era map / location panel + `travel-to`; derive the resonance `place` from `currentLocationCode`.
2. Render NPC capsules (`kind:"NPC"`) with role; vendor → market UI.
3. CHOOSE buttons use `choice.label`.

## Notes
- Geography is seeded representatively (PARNAVAZ fully connected; landmark anchors for later eras)
  and grows as authored data — no client change when more locations are added.
- Not yet wired (future backend): per-vendor inventories, NPC dialogue, combat→siege contribution.
