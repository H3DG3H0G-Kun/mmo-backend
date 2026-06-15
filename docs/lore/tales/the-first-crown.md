# Tale — ფარნავაზის გვირგვინი / The First Crown

> Strand **HISTORY** · Era **PARNAVAZ** (id 1) · Tier **TRUE_TALE** · the founding keystone.
> Design doc (the *why*). The importable data is [content/the-first-crown.json](../../../content/the-first-crown.json).
> Rests on [COSMOLOGY.md](../COSMOLOGY.md). Source: *ქართლის ცხოვრება* (Kartlis Tskhovreba),
> the chronicle of **Leonti Mroveli**, *„ცხოვრება ქართველთა მეფეთა"*.

---

## 1. Why this tale, and why first

This is the **birth of Georgian memory itself**, so it is the right keystone for the whole game.
The chronicle credits **Parnavaz (ფარნავაზ)** — the first king of Kartli (Iberia), traditionally
c. 3rd century BC — not only with uniting the land and raising the cult of **Armazi**, but with
**giving the realm its own writing** (*„ამან ფარნავაზ ... ქმნა მწიგნობრობა ქართული"*). For a
game whose single claim is *what is truly remembered is not lost*, a founding myth in which a
people first learns **to write memory down so it can never be wholly forgotten** is not just
fitting — it is the thesis, dramatized. The Watcher is *present at the moment memory gained a
body.*

**Source honesty (binding, per COSMOLOGY §6.6):** the attribution of the Georgian script to
Parnavaz is the *chronicle's tradition*, debated by modern scholars (the surviving script is
later). We honor it **as the source records it** — because this game is about *memory as it was
held*, not a forensic reconstruction — and we say so plainly here. The Watcher witnesses the
*tradition*; the design is honest about what is chronicle and what is history.

---

## 2. Honest history (what the chronicle records)

- After **Alexander's** incursion into Kartli, the land was held by **აზონ / Azon**, a tyrant
  left behind by the Macedonians, who oppressed the people.
- **Parnavaz**, of the line of **Kartlos / Mtskhetos**, living hidden, is granted a **dream of
  the sun and moon** — a sign of kingship. Hunting, he uncovers a hidden **treasure of the
  Caucasus** that lets him raise a force.
- He allies with **ქუჯი / Kuji of Egrisi** (western Georgia). Together they overthrow Azon and
  **Parnavaz is made the first king of Kartli.**
- As king he raises the great idol/cult of **არმაზი / Armazi** on the mountain by **Mtskheta**
  and strengthens **არმაზციხე / Armaztsikhe**; he organizes the realm under **eristavis**; and,
  by tradition, **establishes Georgian letters and learning.**

Everything below is authored *only* from this; no invented betrayal of the figures.

---

## 3. The Distortion

**The Aspect of the Foreign Yoke (ჩრდილი / Aspect).** Not Azon-the-man — we never make the
historical figure the boss. The Distortion is the *Unremembering wearing the shape of subjugation*:
the pressure that would leave Kartli **nameless and letterless**, a province that forgets it ever
had a crown or an alphabet of its own. Defeating it = *holding the founding true.* It is **warded**,
not slaughtered; victory is that the name and the letters endure.

---

## 4. Beat graph (Saga → Tale → Beats)

Saga **SAGA_FARNAVAZ** — *„ფარნავაზის ხანა / The Age of Parnavaz"* (HISTORY, era 1).
Tale **TALE_FARNAVAZ_CROWN** — *The First Crown* (TRUE_TALE, unlocks era 2 = EARLY_KING).
Resonance (ჟღერადობა): **ERA = PARNAVAZ** AND **PLACE = throne_hall** (the live, demoed anchor).

A branch-and-rejoin graph (exercises CHOOSE + edges), nine beats (the Choose doubles one scene):

```
THE_LAND_UNDER_YOKE (Witness)
        │
THE_DREAM_OF_SUN_AND_MOON (Witness)
        │
THE_HIDDEN_TREASURE (Aid)
        │
CHOOSE_THE_ALLY (Choose) ──"kartli"──> WITH_KARTLI (Witness) ─┐
        └─────────────────"egrisi"──> WITH_EGRISI (Witness) ─┤
                                                              ▼
                                          WARD_THE_FOUNDING (Ward)   ← the Aspect of the Foreign Yoke
                                                              │
                                          THE_FIRST_LETTERS (Restore)  ← the jewel: the script is born
                                                              │
                                          THE_CROWN_IS_REMEMBERED (Witness, terminal) → era 2 opens
```

The **CHOOSE** is a *True-Tale* choice (COSMOLOGY: both branches are true): you walk to the
muster either among the warriors of **Kartli** or those of **Egrisi/Kuji** — two true facets of
one remembered unification — and both rejoin at the Ward. Nothing in history is altered; you
choose only *which truth to witness from.*

The **RESTORE** beat (`THE_FIRST_LETTERS`) is the thematic heart of all nine: the Watcher helps
form the first ქართული letters — memory learning to write itself down. This is the moment the whole
cosmology is stated in-fiction.

---

## 5. The text (Georgian-first intent)

The JSON narration is in English for the running build (the engine stores plain `narration`
text today, and the contract is i18n-ready for a later Georgian field). The **intended primary
language is ქართული**; the per-beat Georgian lines below are the canon to localize toward, and
several are folded into the English narration as quoted phrases the Codex/Matiane will surface:

- `THE_DREAM_OF_SUN_AND_MOON` — *„და იხილა ფარნავაზ ჩუენებით მზე და მთოვარე"* (he beheld in a
  vision the sun and the moon).
- `THE_FIRST_LETTERS` — *„ქმნა მწიგნობრობა ქართული"* (he made Georgian letters / learning).
- `THE_CROWN_IS_REMEMBERED` — *„და იქმნა ფარნავაზ მეფე ქართლისა"* (and Parnavaz became king of
  Kartli).

> **TODO (with the human director):** commission/verify the full ქართული narration for all seven
> beats before this tale is treated as final canon. The JSON ships now so the pipe is provable
> live; the Georgian text is the next pass.

---

## 6. Build / import notes (for a backend session)

- The running DB already contains a **placeholder** seed from `V2__narrative.sql`:
  `SAGA_PARNAVAZ` / `TALE_FIRST_CROWN`, same resonance (`ERA=PARNAVAZ` + `PLACE=throne_hall`).
  This authored tale uses **fresh, non-colliding codes** (`SAGA_FARNAVAZ` / `TALE_FARNAVAZ_CROWN`)
  so it **imports live today** via the Content Studio / `POST /api/admin/content/import` without a
  409 against the placeholder.
- **Recommended cleanup (backend task, not narrative):** retire the `V2` placeholder
  (`TALE_FIRST_CROWN`) so two Heralds don't resonate at `throne_hall`. Until then, expect both to
  appear at that anchor; this real tale supersedes the placeholder in canon.
- `unlocksEraId = 2` preserves the existing progression (completing the founding opens
  EARLY_KING), matching the placeholder's reward.
- Verifies the same live path already proven for სტუმარ-მასპინძელი: admin import → resonate at
  `throne_hall` in PARNAVAZ → enter → Witness/Aid → Choose → Ward → Restore → complete → era 2.
