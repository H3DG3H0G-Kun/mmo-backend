# Era-by-Era Timeline Map — *Watcher* (მცველი)

> The chronological skeleton of the History Layer: which **History / Myth / Word** lives in each
> era. Rests on [COSMOLOGY.md](COSMOLOGY.md). Eras are **data** (rows in `era`), not engine — so
> this map proposes new eras as a backend **seed task**, not a code change. Tale slate detail in
> [tales/](tales/) and [tales/CATALOG_eras-5-13.md](tales/CATALOG_eras-5-13.md).

---

## 1. Seeded eras (live now, ids 1–4)

| id | code | name | era of |
|----|------|------|--------|
| 1 | `PARNAVAZ` | The Age of Parnavaz | founding of Iberia/Kartli, ~3rd c. BC |
| 2 | `EARLY_KING` | The Early Kingdoms | the Iberian kings, pre-Christian |
| 3 | `GOLDEN_AGE` | The Golden Age | Queen Tamar, the apex, 12th–13th c. |
| 4 | `RUSTAVELI` | The Myth-Weave of the Word | the literary/mythic interior (Rustaveli) |

Note: `GOLDEN_AGE` and `RUSTAVELI` overlap in real time (both Tamar's century). `RUSTAVELI` is
treated as the *literary interior layer* of the Golden Age, not a later century — a deliberate
design choice so the WORD strand has a home.

## 2. Proposed new eras (a backend SEED migration — apply to play eras 5–13)

These are **data rows**, ready to seed. Ordinals interleave chronologically; seeding may renumber
existing ordinals or use the scheme below (the `era.ordinal` UNIQUE constraint is the only care).

| id | code | name (ქართული — English) | era of |
|----|------|------|--------|
| 5 | `NINO` | ნინოს ხანა — The Coming of the Light | St Nino, Christianization of Kartli, 4th c. |
| 6 | `VAKHTANG` | ვახტანგის ხანა — The Age of Vakhtang Gorgasali | Vakhtang, founding of Tbilisi, 5th c. |
| 7 | `ABO` | გრძელი ღამე — The Long Night | the Arab period & Emirate of Tbilisi, 7th–9th c. |
| 8 | `BAGRATI` | მიწათა შეკრება — The Gathering of the Lands | unification under Bagrat III, ~1008 |
| 9 | `AGHMASHENEBELI` | აღმაშენებლის ხანა — The Age of David the Builder | David IV, Didgori 1121 |
| 10 | `SUNDERING` | შლა — The Sundering | the Mongol invasions & fragmentation, 13th c. |
| 11 | `SAAKADZE` | დიდი მოურავის ხანა — The Age of the Great Mouravi | Giorgi Saakadze, 17th c. |
| 12 | `EREKLE` | უკანასკნელი სამეფო — The Last Kingdom | Erekle II, Krtsanisi 1795 |
| 13 | `MODERN_WORD` | აღორძინებული სიტყვა — The Reborn Word | 19th-c. national-revival literature |

> When `MODERN_WORD` (13) is seeded, **move სტუმარ-მასპინძელი / Host & Guest from its temporary
> home era 4 → 13** (its true literary home; it was shelved at 4 only because 13 didn't exist yet).

## 3. The Myth strand is trans-era

Myth (ისტორია/მითი/სიტყვა's middle strand) is not bound to one century. Mythic tales (Amirani, the
devis, **დალი/Dali** the hunt-goddess, the **გველეშაპი/dragon**, Vazha's **მინდია/Mindia** the
snake-eater) are **reachable at sacred places without an ERA gate** (resonance = `PLACE`+`STATE`,
no `ERA`), and shelved in the Matiane at a "home" era for ordering. Amirani is homed at era 1 as
primordial memory but reachable from any era at a storm-peak.

---

## 4. What lives in each era (the content atlas)

- **PARNAVAZ (1)** — *History:* the founding, the first king, the birth of the script. *Myth:* the
  primordial Caucasus — **Amirani** (the bound titan), the deep memory. *Word:* the first letters
  themselves. → **The First Crown** ✅, **Amirani** ✅(this batch).
- **EARLY_KING (2)** — *History:* the Iberian kings, the pagan cults of Armazi/Zaden before the
  Light. *Bridge era* (a tale to author next: a pre-Christian king-tale that unlocks NINO).
- **NINO (5)** — *History/Word:* **St Nino**, the grapevine cross, the conversion of Mirian & Nana,
  the raising of **Svetitskhoveli** (the living pillar over the Robe). → **The Light of Kartli**.
- **VAKHTANG (6)** — *History:* **Vakhtang Gorgasali**, the wolf-head king, the founding of
  **Tbilisi** at the warm springs, the great defense. → **The Wolf-Head's City**.
- **ABO (7)** — *History/Word:* the Long Night under the Arab yoke; **Abo of Tiflis**, the convert
  martyr, patron of the city (hagiography of Ioane Sabanisdze). The Unremembering at its strongest.
  → **Abo of Tiflis**.
- **BAGRATI (8)** — *History:* the gathering of the splintered lands into one crown (Bagrat III).
  *(Tale to author next: the unification.)*
- **AGHMASHENEBELI (9)** — *History:* **David the Builder**, **Didgori (1121)**, the recovery of
  Tbilisi, Gelati. → **Didgori**.
- **GOLDEN_AGE (3)** — *History:* **Queen Tamar**, Basiani, the apex. → **The Tamar Crown**.
- **RUSTAVELI (4)** — *Word:* **ვეფხისტყაოსანი**, the great Saga, the Kajeti throughline. →
  **The Knight in the Panther's Skin** (opening; multi-tale Saga). Also the literary home of
  **Host & Guest** until era 13 exists.
- **SUNDERING (10)** — *History:* the Mongol darkness; **Demetre II the Self-Sacrificer**, who gave
  his life for his people's memory — the Watcher's own mirror. → **The Self-Sacrificer**.
- **SAAKADZE (11)** — *History:* **Giorgi Saakadze**, the Great Mouravi; the tragedy of a divided
  homeland and the cost of unity; **Marabda (1625)**. → **The Great Mouravi**.
- **EREKLE (12)** — *History:* **Erekle II**, the last kingdom; **Krtsanisi (1795)** and the
  **Three Hundred Aragvians**; the burning of Tbilisi. → **Krtsanisi**.
- **MODERN_WORD (13)** — *Word:* the national revival; Vazha-Pshavela's highland humanism. →
  **Aluda Ketelauri** (+ the future home of **Host & Guest**; Ilia, Akaki, "Suliko" to come).

---

## 5. Authored this batch (import readiness)

| Tale | Era (home) | Imports now? |
|------|-----------|--------------|
| Amirani | PARNAVAZ (1) | ✅ yes |
| The Tamar Crown | GOLDEN_AGE (3) | ✅ yes |
| The Knight in the Panther's Skin (opening) | RUSTAVELI (4) | ✅ yes |
| The Light of Kartli (St Nino) | NINO (5) | after era 5 seeded |
| The Wolf-Head's City (Vakhtang) | VAKHTANG (6) | after era 6 seeded |
| Abo of Tiflis | ABO (7) | after era 7 seeded |
| Didgori (David the Builder) | AGHMASHENEBELI (9) | after era 9 seeded |
| The Self-Sacrificer (Demetre) | SUNDERING (10) | after era 10 seeded |
| The Great Mouravi (Saakadze) | SAAKADZE (11) | after era 11 seeded |
| Krtsanisi (Erekle) | EREKLE (12) | after era 12 seeded |
| Aluda Ketelauri | MODERN_WORD (13) | after era 13 seeded |

> **One backend task unlocks eight tales:** seed eras 5–13 (§2). The unlock chain
> (`unlocksEraId`) is a light **tuning pass** — bridges at EARLY_KING→NINO and the BAGRATI era are
> intentionally left for the next authoring round (a pre-Christian king tale and the unification).
