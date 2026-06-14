# REST contract v1

REST over HTTP for account/meta and turn-based actions. JSON bodies. Auth via
`Authorization: Bearer <jwt>` (the JWT is issued by auth-service; world-service verifies it
with the shared signing config). Errors use RFC-9457 `application/problem+json`
(`{ "status", "detail", ... }`); validation failures add an `errors` map.

Base URLs (local dev): auth-service `http://localhost:8080`, world-service `http://localhost:8090`.

> The backend is the source of truth. Clients send intentions; the backend validates and decides.

## auth-service

### POST /api/auth/register
Body: `{ "username": "watcher", "email": "w@x.com" (optional), "password": "min8chars" }`
- `201` → `TokenResponse` (see below). `409` username taken. `400` validation.

### POST /api/auth/login
Body: `{ "username": "watcher", "password": "..." }`
- `200` → `TokenResponse`. `401` bad credentials.

### GET /api/auth/me  *(auth)*
- `200` → `AccountResponse`. `401` missing/invalid token.

```
TokenResponse  = { accessToken, tokenType:"Bearer", expiresInSeconds, account: AccountResponse }
AccountResponse= { id, username, email, roles:[string], createdAt }
```

## world-service

All routes require auth unless noted. `characterId` must belong to the caller's account.

### Characters
- `POST /api/characters` — body `{ "name": "3-24 chars [A-Za-z0-9_]" }` → `201` `CharacterResponse`.
  `409` name taken / too many (max 5).
- `GET /api/characters` → `200` `[CharacterResponse]` (the caller's).

```
CharacterResponse = { id, name, currentEra: EraView, createdAt }
EraView           = { id, code, name, ordinal }
```

### World / Timeline
- `GET /api/world/eras?characterId=<uuid>` → `200` `[{ id, code, name, ordinal, unlocked }]`.
- `POST /api/world/travel` — body `{ characterId, eraCode }` → `200` `CharacterResponse`.
  `409` era locked. `404` era unknown.
- `GET /api/world/timeline` → `200` `[{ eraId, code, name, ordinal, restoredCount }]` (server-wide).

### Narrative — solo
- `GET /api/narrative/resonances?characterId=<uuid>&place=<code>&states=A,B`
  → `200` `[{ taleId, code, title, tier, sagaTitle, status }]` (Tales whose Resonance opens here).
- `POST /api/narrative/tales/{taleCode}/enter` — body `{ characterId, place, states:[..] }`
  → `200` `TaleState`. `409` resonance closed. `404` tale unknown.
- `POST /api/narrative/tales/{taleCode}/advance` — body `{ characterId, choiceKey? }`
  → `200` `TaleState`. `409` no active progress. `422` invalid/missing choiceKey.

### Narrative — co-op (party-shared)
- `POST /api/narrative/party/tales/{taleCode}/start` — body `{ characterId, place, states:[..] }`
  → `200` `TaleState`. `403` not party leader. `409` not in party / already active. `409` resonance closed.
- `POST /api/narrative/party/advance` — body `{ characterId, choiceKey? }` → `200` `TaleState`.
  `404` no active instance.
- `GET /api/narrative/party/instance?characterId=<uuid>` → `200` `TaleState`. `404` none.

```
TaleState = { taleId, code, title, status:"IN_PROGRESS"|"COMPLETED",
              currentBeat: { id, code, narration, interaction, terminal } | null,
              unlockedEraId: int | null }
interaction ∈ WITNESS | AID | WARD | CHOOSE | RESTORE
```

### Party
- `POST /api/party` — body `{ characterId, maxSize? (2-10, default 5) }` → `201` `PartyView`. `409` already in a party.
- `POST /api/party/{partyId}/join` — body `{ characterId }` → `200` `PartyView`. `409` full/closed. `404` party.
- `POST /api/party/leave` — body `{ characterId }` → `200` `PartyView` (new state) or `204` (party disbanded). `404` not in a party.
- `POST /api/party/disband` — body `{ characterId }` → `204`. `403` not leader. `404` not in a party.
- `GET /api/party?characterId=<uuid>` → `200` `PartyView`. `404` not in a party.

```
PartyView = { id, leaderCharacterId, status:"OPEN"|"CLOSED"|"DISBANDED", maxSize,
              members: [{ characterId, name, leader }] }
```

### Admin — content authoring (ROLE_ADMIN)
- `POST /api/admin/content/import` — body is a content document (Saga + Tales + Beats + Edges +
  Triggers); imports it as playable content. `201` → `{ sagaCode, tales, beats }`.
  `409` saga/tale code already exists. `422` malformed (bad enum / dangling beat ref).
  Non-admins are denied (currently `401`). Body shape:

```
{ "saga": { "code", "title", "strand":"HISTORY|MYTH|WORD", "eraId", "ordinal" },
  "tales": [ { "code", "title", "tier":"TRUE_TALE|ECHO", "ordinal", "unlocksEraId",
    "beats":   [ { "code", "ordinal", "narration", "interaction":"WITNESS|AID|WARD|CHOOSE|RESTORE", "terminal" } ],
    "edges":   [ { "from", "to", "choiceKey" } ],
    "triggers":[ { "type":"ERA|PLACE|STATE|PRIOR_TALE", "value" } ] } ] }
```
