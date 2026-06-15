# WORLD DESIGN — *Watcher* (მცველი)

> The **systems spec for the world as data**: the units & coordinate convention, the
> location/region model, the travel graph, towns & vendors, NPCs, and the rates tables.
> Authored so BOTH the backend (seed/serve) and the Unity client (render) can rely on it.
>
> **Project law (do not violate):** the backend is authoritative; **the world is data, never
> hardcoded logic.** Every location, link, NPC and rate below is a row to be seeded, not Java.
> This doc *proposes* shapes; the **backend session finalizes the exact schema/endpoints**.
>
> Grounded in what already exists — it invents **no conflicting codes**. Reconciled against:
> `era` (V1), `place_of_power` (V7), `item_def`/economy (V8, `EconomyService`), `world_echo`
> (V9), `enemy_def`/combat (V10), the narrative `trigger_condition` PLACE/STATE model (V2), and
> the authored content in `content/*.json`. Companion docs: [GAME_DESIGN.md](../GAME_DESIGN.md)
> (systems), [lore/COSMOLOGY.md](../lore/COSMOLOGY.md) (soul), [lore/ERAS.md](../lore/ERAS.md)
> (the era atlas).

---

## 0. Status — vertical slice first

This document fully specs **ONE era — PARNAVAZ** — as importable data: one region with real
bounds, two towns with vendors, the existing `throne_hall` and `armazi_mount`/`ARMAZTSIKHE`
places, a myth `peak`, NPCs, enemies, and intra-region travel — so backend + client can build
something real end-to-end. Eras 2–13 reuse the **same shapes**; their regions are a later
authoring round (§9 pacing). Seed-ready JSON lives beside this file in
[`docs/design/world/`](world/).

---

## 1. UNITS & COORDINATE CONVENTION  *(the contract both sessions rely on)*

This section is **normative**. Backend stores world geometry; the client renders it; these are
the rules that keep them in agreement.

| Rule | Value |
|------|-------|
| **Unit** | 1 world-unit = **1 metre**. All `x`, `y`, `radius`, distances are metres. |
| **Backend axes** | A location is a 2-D point `(x, y)` on the era's ground plane. `x` = east(+)/west(−); `y` = north(+)/south(−). |
| **Client mapping** | Backend `(x, y)` → Unity `(x, 1, y)`. **Unity `z` = backend `y`**; Unity `y` is *up* (fixed `1`, capsules stand on the plane). This already matches `WorldManager.ToWorld(x,y) => new(x, 1f, y)`. |
| **Origin** | Per era, `(0,0)` is the **era's hub-town centre** (the spawn anchor). For PARNAVAZ, `(0,0)` = **Mtskheta** centre; new Watchers spawn at `(0,0)`. |
| **Anchor test** | A Watcher is "at" a location when their **XZ distance to the location centre ≤ `radius`**. Standing inside reports that location's `code` as the narrative `place`. (Exactly the current `WorldManager.NearestPlace` logic; ties broken by nearest centre.) |
| **Era is a layer, not geometry** | Travelling between eras (existing `POST /api/world/travel`) changes **which locations/tales/spawns are active**, on the *same* coordinate frame — not new map geometry (yet). A hill at `(−34,20)` is the same hill in every era; what stands on it changes. |
| **Region bounds** | Each era declares an AABB `{minX,minY,maxX,maxY}` for the client's camera/minimap extent. PARNAVAZ: `{ -60, -30, 60, 150 }`. |
| **Movement** | Walk speed is **5 m/s** (client `WorldManager.MoveSpeed`); travel-time numbers below assume it. |

> **Why centre on the hub-town:** it keeps the existing hardcoded `throne_hall` at `(6,6) r=4`
> valid (it falls just NE of Mtskheta centre), so the seeded First Crown tale keeps resonating
> with zero client changes during migration.

---

## 2. THE LOCATION / REGION MODEL

Today there is **no location/anchor table** — `place` is a bare string the *client* sends, and
the only geometry (`throne_hall @ (6,6) r4`) is hardcoded client-side. This spec replaces that
with **server-owned locations** the client renders. One row type unifies four concerns that are
currently scattered (narrative `PLACE` strings, `place_of_power` rows, towns, hunting grounds).

**`location`** (proposed) — the atom of the world:

```
location = {
  code:    string,   // UNIQUE, lowercase; IS the narrative `place` value (V2 trigger PLACE)
  name:    string,   // display (Georgian-first per COSMOLOGY §7)
  eraCode: string,   // owning era (era.code); null = trans-era (myth, reachable in any era)
  type:    "TOWN" | "LANDMARK" | "PLACE_OF_POWER" | "WILDS",
  x: number, y: number, radius: number,        // metres, §1 convention
  siegePlaceCode: string | null,  // links to place_of_power.code if contestable (V7)
  folk:    string | null,         // TOWN only: regional identity for client ambient (§3)
  forageRegion: string | null     // key into the forage table (§5B); null = not forageable
}
```

**Reconciliation rules (honour existing codes):**

- The narrative `place` a tale triggers on **equals a `location.code`.** `throne_hall`, `peak`,
  `armazi_mount` are real authored PLACE values (`content/the-first-crown.json`,
  `amirani.json`, `the-watchfire-of-armazi.json`) → each becomes a `location` row with that
  exact code.
- A `PLACE_OF_POWER` location sets `siegePlaceCode` to an existing `place_of_power.code`
  (`ARMAZTSIKHE`, `NEKRESI`). **`armazi_mount` and `ARMAZTSIKHE` are the same hill** (Armaztsikhe
  fortress *is* the Armazi mount above Mtskheta): one location, `siegePlaceCode=ARMAZTSIKHE`
  (contested in era 1), narrative `place=armazi_mount` (the Watchfire tale, era 2). Era-as-layer
  (§1) makes one hill do both jobs.
- `peak` has `eraCode=null` (trans-era myth): Amirani resonates on `PLACE=peak` **+ `STATE=STORM`,
  no ERA** — reachable from any era at a storm-peak, exactly as authored.

### 2.1 PARNAVAZ region — locations

Region: the **Mtskheta heartland** — the Mtkvari–Aragvi confluence where Iberia/Kartli was
founded. Bounds `{ -60,-30, 60,150 }`. Full data: [`world/parnavaz.locations.json`](world/parnavaz.locations.json).

| code | name | type | era | x | y | r | role |
|------|------|------|-----|---|---|---|------|
| `mtskheta` | მცხეთა — Mtskheta, Seat of Kartli | TOWN | PARNAVAZ | 0 | 0 | 14 | hub / spawn; Kartlian folk; vendor + herald |
| `throne_hall` | The Throne-Hall of the First Crown | PLACE_OF_POWER | PARNAVAZ | 6 | 6 | 4 | First Crown resonance *(existing coords)* |
| `armazi_mount` | არმაზი — The Mount of Armazi | PLACE_OF_POWER | PARNAVAZ | -34 | 20 | 9 | siege `ARMAZTSIKHE` (era1) + Watchfire `place` (era2) |
| `aragvi_marches` | The Frayed Marches of the Aragvi | WILDS | PARNAVAZ | 12 | 52 | 26 | hunting ground; Nisli wisps + Echo distortion |
| `pasanauri` | ფასანაური — Pasanauri, the Aragvi Hold | TOWN | PARNAVAZ | 24 | 96 | 10 | highland town; Mtiuli folk; smith-vendor |
| `peak` | The Storm-Peak of the Chained One | LANDMARK | *trans-era* | 38 | 132 | 6 | Amirani myth (`PLACE=peak`+`STATE=STORM`) |

> Two real, respectfully-handled folk identities in the slice: **Kartlian** (lowland heartland,
> Mtskheta) and **Mtiuli** (Mtiuleti highlanders of the upper Aragvi, Pasanauri). Both are real
> Georgian regions; client ambient differs by `folk` (§3).

---

## 3. TOWNS, FOLK IDENTITY & VENDORS

A `TOWN` location carries a `folk` tag the client uses to pick **in-town ambient** (voices,
polyphony register, banner motif — see SOUND_DIRECTION / ART_DIRECTION). It is *flavour data*,
never a faction or a mechanical advantage (honours COSMOLOGY §6 — belonging, not division).

| town | folk | identity (for ambient) | vendor |
|------|------|------------------------|--------|
| `mtskheta` | `KARTLIAN` | The Kartli heartland — the royal seat, measured and ceremonial. | **Kviria, the Mtskheta Trader** |
| `pasanauri` | `MTIULI` | Upper-Aragvi highlanders — terse, iron-and-stone, a smith's town. | **The Smith of the Aragvi** |

**Vendor rosters** (full data: [`world/parnavaz.vendors.json`](world/parnavaz.vendors.json)).
Vendors are NPC *static shops* (distinct from the player-to-player market of `EconomyService`).
Prices reference the **base values** in §5A; the player market stays free-priced.

| vendor | at | sells (code @ gold) | buys (code @ gold) |
|--------|----|---------------------|--------------------|
| Kviria | `mtskheta` | `MOUNTAIN_HERB @6` | gatherables @ base×0.8 (`MOUNTAIN_HERB@4`, `IRON_ORE@6`) |
| Smith | `pasanauri` | `IRON_ORE @10` (highland premium) | `MOUNTAIN_HERB @5` |

> The slice catalog is intentionally tiny (the 3 seeded items). Rosters grow as the item
> catalog grows; the *shape* is what matters now.

---

## 4. NPCs

NPCs are **server-owned world entities streamed in the presence feed** alongside players, so the
client can render and interact with them generically. Today only players ride the presence
stream; this adds a `kind`/`role` discriminator (§ Handoff-client).

**`npc`** (proposed):

```
npc = {
  code:        string,   // UNIQUE
  name:        string,
  role:        "VENDOR" | "HERALD" | "TOWNSFOLK" | "GUARD",
  locationCode:string,   // where it stands (location.code)
  offsetX: number, offsetY: number,   // metres from the location centre (§1 frame)
  behavior:    "STATIC" | "WANDER",   // WANDER = small idle roam inside the location radius
  interactive: boolean,
  interaction: "MARKET" | "TALE" | "DIALOGUE" | "NONE",
  taleCode:    string | null,   // TALE interaction: which tale the Herald opens
  vendorCode:  string | null,   // MARKET interaction: which vendor roster
  folk:        string | null    // TOWNSFOLK/vendor flavour
}
```

The presence stream carries each NPC as `{ id, kind:"NPC", role, name, x, y, interactive,
interactionCode }` where `x,y = location centre + offset`.

### 4.1 PARNAVAZ NPCs

Full data: [`world/parnavaz.npcs.json`](world/parnavaz.npcs.json).

| code | name | role | at | behavior | interaction |
|------|------|------|----|---------:|-------------|
| `HERALD_FIRST_CROWN` | მახარობელი — Herald of the First Crown | HERALD | `throne_hall` | STATIC | TALE → `TALE_FARNAVAZ_CROWN` |
| `HERALD_STORM_PEAK` | The Elder of the Storm-Peak | HERALD | `peak` | STATIC | TALE → `TALE_AMIRANI_BOUND` |
| `VENDOR_KVIRIA` | Kviria, the Mtskheta Trader | VENDOR | `mtskheta` | STATIC | MARKET → `mtskheta` |
| `VENDOR_SMITH` | The Smith of the Aragvi | VENDOR | `pasanauri` | STATIC | MARKET → `pasanauri` |
| `GUARD_ARMAZI` | The Warden of the Mount | GUARD | `armazi_mount` | STATIC | DIALOGUE (siege flavour) |
| `FOLK_KARTLI_1..3` | Kartlian townsfolk | TOWNSFOLK | `mtskheta` | WANDER | NONE (ambient) |
| `FOLK_MTIULI_1..2` | Highland folk | TOWNSFOLK | `pasanauri` | WANDER | NONE (ambient) |

> **Heralds are resonance-gated, not always shown.** A HERALD only manifests when its tale's
> Resonance is open for that Watcher (the `GET /api/narrative/resonances` result already encodes
> this). `HERALD_STORM_PEAK` therefore appears at `peak` **only during `STATE=STORM`.** This keeps
> the GAME_DESIGN §2 promise: the Herald is "the first line of the story given form," not a
> permanent quest-marker.

---

## 5. RATES TABLES

Formalises existing numbers and authors the missing ones. **Bold = already live in code**;
others are proposals for the backend session. Full data: [`world/rates.json`](world/rates.json).

### 5A. Item reference values  *(new — proposes `item_def.base_value`)*

The player market (`shop_listing`) stays **free-priced**; these are the vendor buy/sell anchor
and a sane economic baseline.

| item | base value (gold) | source |
|------|------------------:|--------|
| `MOUNTAIN_HERB` | 5 | forage (lowland) |
| `IRON_ORE` | 8 | forage (wilds/highland) |
| `MEMORY_RELIC` | 40 | tale reward (not forageable) |

### 5B. Forage yields per region  *(extends the current global forage)*

**Live today:** `forage()` grants **3** of a **random gatherable** (`FORAGE_QUANTITY=3`), with
no region awareness. Proposal: weight the draw by the forager's `location.forageRegion`.

| forageRegion | item weights | qty | notes |
|------|------|----:|------|
| `LOWLAND` (mtskheta outskirts) | `MOUNTAIN_HERB 70 / IRON_ORE 30` | 3 | herb country |
| `WILDS` (aragvi_marches) | `IRON_ORE 60 / MOUNTAIN_HERB 40` | 3 | ore in the frayed marches |
| `HIGHLAND` (pasanauri) | `IRON_ORE 70 / MOUNTAIN_HERB 30` | 4 | richest yield, the iron hold |

> Until the backend adds region weighting, the live global `qty=3 random gatherable` is the
> safe fallback and remains valid.

### 5C. Economy constants

| constant | value | status |
|------|------:|------|
| Starting gold | **100** | **live** (`EconomyService.STARTING_GOLD`) |
| Forage quantity (default) | **3** | **live** (`FORAGE_QUANTITY`) |
| Market fee (player→player) | **5 %** | **proposed** — a gold *sink*: buyer pays `total`, seller receives `total×0.95`, 5 % burned. Currently **0 %** (`buy()` transfers full total). Backend's call. |

### 5D. Enemy stats per era + spawns  *(combat = the WARD verb)*

| code | name | maxHp | atk | gold | status |
|------|------|------:|----:|----:|------|
| `NISLI_WISP` | Nisli Wisp | 40 | 6 | 12 | **proposed** — common WILDS trash |
| `ECHO_OF_THE_CROWN` | Echo of the Crown | **100** | **10** | **50** | **live** (V10) — First Crown WARD boss |
| `ASPECT_OF_BETRAYAL` | Aspect of Betrayal | **160** | **18** | **90** | **live** (V10) — reserved for EARLY_KING / Oath of Kings |

**PARNAVAZ spawns** (`world/rates.json`):

| where | enemy | density (concurrent) | respawn | kind |
|-------|-------|---------------------:|--------:|------|
| `aragvi_marches` (WILDS) | `NISLI_WISP` | 6 | 45 s | free-roam, spread in radius |
| First Crown `WARD_THE_FOUNDING` beat | `ECHO_OF_THE_CROWN` | 1 | n/a | **encounter-bound** (tale instance, not world-spawned) |

### 5E. World Echo goal sizes  *(seeds the unseeded `world_echo.goal`)*

| tale | echo goal (contribution) | rationale |
|------|------:|------|
| `TALE_FARNAVAZ_CROWN` | 500 | small starter set-piece for the first public Echo |

### 5F. Resonance / era-unlock pacing

| era | gateway tale | unlocks | shape |
|-----|------|------|------|
| PARNAVAZ | `TALE_FARNAVAZ_CROWN` (`throne_hall`) | EARLY_KING | **live** `unlocksEraId=2`; 8 beats, one CHOOSE; ~15–25 min |
| *(trans-era)* | `TALE_AMIRANI_BOUND` (`peak`+STORM) | — (myth → Codex only) | **live** `unlocksEraId=null` |
| EARLY_KING | `TALE_WATCHFIRE_OF_ARMAZI` (`armazi_mount`) | NINO | **live** `unlocksEraId=5` |

Pacing target: **one gateway (era-unlocking) HISTORY tale per era**, 6–8 beats, plus trans-era
MYTH and WORD tales reachable at places without an ERA gate. A Watcher advances the timeline by
clearing the gateway; everything else is breadth.

---

## 6. THE TRAVEL GRAPH

Two kinds of travel, reconciled with the existing system:

1. **Era travel (cross-layer) — already built.** `POST /api/world/travel {characterId, eraCode}`
   moves the Watcher between era *layers* (gated; free; the only inter-era "teleport"). Out of
   scope to change.
2. **Intra-era travel (this spec).** On a single era's plane the world is **seamless — you walk
   it** (L2 doctrine: the journey is content, no fast-travel firehose). The graph below is
   therefore mostly informational (connectivity for the minimap) plus a **sparse, paid teleport**
   for town-to-town convenience.

**`travel_link`** (proposed):

```
travel_link = {
  fromCode: string, toCode: string,   // location.code (undirected unless noted)
  mode:     "WALK" | "TELEPORT",
  costGold: number,        // WALK = 0; TELEPORT = gold sink
  travelSeconds: number,   // WALK ≈ distance / 5 m/s; TELEPORT = 0 (instant)
  eraLocked: boolean       // reserved for future cross-era geometry; false in the slice
}
```

### 6.1 PARNAVAZ travel graph

Full data: [`world/parnavaz.travel.json`](world/parnavaz.travel.json). WALK costs are the
straight-line metres ÷ 5 m/s (the client may route around obstacles later).

```
   peak (38,132)
     │  WALK ~8s
   pasanauri (24,96) ──────────────┐
     │  WALK ~9s                    │  TELEPORT 20g (memory-road, town↔town)
   aragvi_marches (12,52)          │
     │  WALK ~11s                   │
   mtskheta (0,0) ─────────────────┘
     ├─ WALK ~2s  → throne_hall (6,6)      (inside the town zone)
     └─ WALK ~8s  → armazi_mount (-34,20)  (across the river)
```

| from | to | mode | cost | ~time | note |
|------|----|------|-----:|------:|------|
| `mtskheta` | `throne_hall` | WALK | 0 | 2 s | same town zone |
| `mtskheta` | `armazi_mount` | WALK | 0 | 8 s | cross the Mtkvari |
| `mtskheta` | `aragvi_marches` | WALK | 0 | 11 s | north into the wilds |
| `aragvi_marches` | `pasanauri` | WALK | 0 | 9 s | up the gorge |
| `pasanauri` | `peak` | WALK | 0 | 8 s | the climb |
| `mtskheta` | `pasanauri` | **TELEPORT** | **20** | 0 s | the one paid memory-road; town↔town only |

> Only **one** teleport in the whole region, and it is town-to-town and gold-costed — the wilds,
> the mount, and the peak are reachable **only on foot.** That is the L2 soul: contested/sacred
> ground is *travelled to*, never warped to.

---

## 7. HANDOFF — (a) BACKEND SESSION

What to model, seed, and serve. All of it is **data + thin generic engines**, consistent with
existing codes; nothing here asks for hardcoded story.

1. **`location` table + seed** (§2). New table keyed by `code`; seed the 6 PARNAVAZ rows from
   [`world/parnavaz.locations.json`](world/parnavaz.locations.json). It **subsumes the
   client-hardcoded anchors** — `throne_hall` must keep `(6,6,4)` so the live First Crown tale
   keeps resonating through migration. Set `siegePlaceCode` on `armazi_mount` → existing
   `ARMAZTSIKHE` (V7).
2. **Serve locations:** `GET /api/world/locations?eraCode=<code>` → `[location]` (plus trans-era
   rows, `eraCode=null`). The client builds anchors/markers from this instead of hardcoding.
3. **`npc` table + presence integration** (§4). Seed the PARNAVAZ NPCs
   ([`world/parnavaz.npcs.json`](world/parnavaz.npcs.json)). **Emit NPCs on the presence/WS
   stream** with `kind:"NPC", role, name, x, y, interactive, interactionCode`. Heralds are
   filtered by live Resonance (reuse the `resonances` evaluator) and by `STATE` (e.g. storm) so
   `HERALD_STORM_PEAK` only appears in a storm.
4. **`travel_link` table + endpoint** (§6). Seed PARNAVAZ links
   ([`world/parnavaz.travel.json`](world/parnavaz.travel.json)); add `GET
   /api/world/travel-links?eraCode=` and a `POST /api/world/teleport {characterId, toCode}` that
   validates a TELEPORT link, debits `costGold`, and repositions. Keep era travel as-is.
5. **Rates** (§5): add `item_def.base_value` (5A); add region-weighted forage keyed by
   `location.forageRegion` (5B, fallback = current global random); decide the **5 % market fee**
   sink in `buy()` (5C); add `enemy_def NISLI_WISP` (40/6/12) and a **spawn table** (location →
   enemy, density, respawn) for WILDS (5D); seed `world_echo.goal=500` for `TALE_FARNAVAZ_CROWN`
   (5E). No change to combat math, just data.
6. **Decoupling note:** the narrative `PLACE` value is *already* a free string — pointing it at
   `location.code` needs **no narrative-engine change**, only that the client sends the code of
   the location it stands in (which it can now learn from the served locations).

## 7b. HANDOFF — (b) CLIENT SESSION  *(this is my lane next)*

The coordinate contract and the new entities to render. **Nothing renders until the backend
serves the tables above** (consistent with the existing "blocked on backend world epic" flag).

1. **Coordinate convention = §1.** No change to the mapping — `ToWorld(x,y)=>(x,1,y)` is already
   correct. 1 unit = 1 m; spawn at era origin `(0,0)`; anchor test = XZ-distance ≤ radius
   (already implemented in `WorldManager.NearestPlace`).
2. **Stop hardcoding anchors.** Replace the inline `throne_hall @ (6,6,4)` with anchors built
   from `GET /api/world/locations?eraCode=`. Render markers by `type`: TOWN (ring + name),
   PLACE_OF_POWER (the existing gold ring), WILDS (subtle boundary), LANDMARK (peak marker). Keep
   reporting the nearest location `code` as `place` to the narrative API — unchanged behaviour,
   now data-driven.
3. **NPC capsules by `role`.** Read `kind:"NPC"` entities from the presence stream and render a
   capsule per `role` (distinct tint/label: VENDOR, HERALD, GUARD, TOWNSFOLK). When `interactive`,
   show an interact prompt that routes by `interactionCode`: MARKET → open the existing
   MarketPanel; TALE → the existing Herald/tale-enter flow; DIALOGUE → a simple line panel.
   WANDER NPCs lerp gently inside their location radius.
4. **Intra-era travel.** Walking already works. Add: a minimap/markers from the locations list;
   a teleport affordance on TELEPORT `travel_link`s (town↔town) that calls `POST
   /api/world/teleport` and shows the `costGold` (reuse the Toast for the 20g memory-road).
5. **Folk ambient.** Use `location.folk` (KARTLIAN / MTIULI) to switch in-town ambient via the
   existing `SoundDirector` hooks — flavour only.

---

## 8. WHAT'S NEXT (after the slice proves out)

Same shapes, more rows: each subsequent era gets a region (bounds + hub-town origin), its
authored `PLACE` codes become `location`s, its `place_of_power` (e.g. `NEKRESI` for EARLY_KING)
gets coordinates, vendors take on real regional folk (Kakhetian, Gurian, Svan, Megrelian…), and
spawns scale with enemy tiers. The engine never changes — **we add data.** That is the whole
design: systems are code, the world is data.
