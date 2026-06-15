# Backend hand-off — seed eras 5–13 (unlocks the authored tale slate)

> **For a backend session.** This is authored **data**, not engine work: it adds rows to the
> existing `era` table (see `services/world-service/.../V1__world_init.sql`) so the chronological
> tales in `content/` can import. Turn it into the next Flyway migration (e.g. `V11__eras.sql`).
> After this lands, every package in `content/` imports cleanly. Source map: [ERAS.md](ERAS.md).

---

## 1. New era rows to insert

The `era` schema is `(id int PK, code text UNIQUE, name text, ordinal int UNIQUE, default_unlocked bool)`.
New eras (ids 5–13), none `default_unlocked` (they're reached via the timeline engine / tale unlocks):

| id | code | name |
|----|------|------|
| 5 | `NINO` | ნინოს ხანა — The Coming of the Light |
| 6 | `VAKHTANG` | ვახტანგის ხანა — The Age of Vakhtang Gorgasali |
| 7 | `ABO` | გრძელი ღამე — The Long Night |
| 8 | `BAGRATI` | მიწათა შეკრება — The Gathering of the Lands |
| 9 | `AGHMASHENEBELI` | აღმაშენებლის ხანა — The Age of David the Builder |
| 10 | `SUNDERING` | შლა — The Sundering |
| 11 | `SAAKADZE` | დიდი მოურავის ხანა — The Age of the Great Mouravi |
| 12 | `EREKLE` | უკანასკნელი სამეფო — The Last Kingdom |
| 13 | `MODERN_WORD` | აღორძინებული სიტყვა — The Reborn Word |

## 2. The ordinal question (one decision to make)

`era.ordinal` is **UNIQUE** and is the timeline's chronological order. The existing rows are
`PARNAVAZ=1, EARLY_KING=2, GOLDEN_AGE=3, RUSTAVELI=4`, but chronologically Nino/Vakhtang/Abo/
Bagrati/David all fall *before* the Golden Age. Two options:

- **Recommended — renumber for true chronology** (updates two existing rows, `GOLDEN_AGE 3→8`,
  `RUSTAVELI 4→9`):

  | ordinal | era | | ordinal | era |
  |--|--|--|--|--|
  | 1 | PARNAVAZ | | 8 | GOLDEN_AGE *(was 3)* |
  | 2 | EARLY_KING | | 9 | RUSTAVELI *(was 4)* |
  | 3 | NINO | | 10 | SUNDERING |
  | 4 | VAKHTANG | | 11 | SAAKADZE |
  | 5 | ABO | | 12 | EREKLE |
  | 6 | BAGRATI | | 13 | MODERN_WORD |
  | 7 | AGHMASHENEBELI | | | |

  Do the UPDATEs and INSERTs in one migration; mind the UNIQUE constraint (e.g. bump existing rows
  to a temporary high range first, then set final values).

- **Simpler — append** new eras as ordinals 5–13 in id order and accept that timeline ordering no
  longer equals strict chronology. Fine for now if the engine only uses ordinal for gating, not
  display order.

This is a backend/product call — recommend the renumber so the Living Timeline reads in true
historical order.

## 3. Import order after seeding

Eras have FK refs from `saga.era_id` and `tale.unlocks_era_id`, so **seed eras first, then import
the JSON.** All packages in `content/` (Content Studio `http://localhost:18090/studio/` or
`POST /api/admin/content/import`) become importable. The five that need no new era
(First Crown, Host & Guest, Amirani, Tamar, Vepkhistqaosani, plus the two new MYTH tales Dali &
Kopala) already import today.

## 4. Two cleanup items already flagged in the tale docs

- Retire the `V2` placeholder `TALE_FIRST_CROWN` / `SAGA_PARNAVAZ` (superseded by `SAGA_FARNAVAZ`).
- If the E13 demo import of „სტუმარ-მასპინძელი" used codes colliding with `SAGA_STUMARMASPINDZELI`
  / `TALE_HOST_AND_GUEST`, retire the demo row.
- When `MODERN_WORD` (13) exists, optionally move Host & Guest's `saga.era_id` 4 → 13 (its true home).
