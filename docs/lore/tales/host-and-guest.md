# Tale — სტუმარ-მასპინძელი / Host and Guest

> Strand **WORD** · home era **RUSTAVELI** (id 4, "The Myth-Weave of the Word") · resonates
> anywhere on a storm-mountain · Tier **TRUE_TALE**. Design doc (the *why*). Importable data:
> [content/host-and-guest.json](../../../content/host-and-guest.json). Rests on
> [COSMOLOGY.md](../COSMOLOGY.md). Source: **ვაჟა-ფშავela, „სტუმარ-მასპინძელი" (1893).**

---

## 1. Why this tale

This is the emotional ceiling of the whole game and the truest test of our design law *"the
Watcher never rewrites history or slays its heroes."* Vazha-Pshavela's poem is a towering
humanist tragedy: in the Caucasus highlands, where Kist (Muslim) and Khevsur (Christian) are
locked in blood-feud, the Kist **ჯოყოლა (Jokola)** gives shelter to a stranger under the holiest
law of the mountains — **სტუმარი ღვთისაა, the guest is God's** — only to discover the guest is
**ზვიადაური (Zviadauri)**, his people's deadliest enemy. The community (**თემი / temi**) seizes
and slays the guest on a kinsman's grave; Jokola, who kept faith, is dishonored among his own;
his wife **აღაზა (Aghaza)** grieves the violated law. The poem ends with nature itself mourning
the cruelty of men.

It is the perfect WORD-strand keystone because **the Watcher cannot and must not fix it.**
Zviadauri dies — that is the canon. What the Watcher does is *witness it truly* and *ward its
meaning*, which is exactly what this game is about.

---

## 2. The hard design problem, and the solution

**The Watcher does NOT save Zviadauri.** Letting the player "win" by preventing his death would
desecrate the poem. So the five Interactions are bent entirely toward *witnessing and meaning*:

- **Witness** the holiness of the guest-law, the recognition, the refusal, the killing — without
  looking away. (Seeing truly *is* keeping, per COSMOLOGY §1.)
- **Aid** only the sacred act of hospitality itself (laying the guest-table) — never the violence.
- **Ward** is redefined here: you do not defend Zviadauri's *life*; you defend the *memory's
  meaning* against the **Aspect of Vengeance** (§3) — the Distortion that would have this night
  remembered as nothing but hatred, stripping the death of dignity and the guest-law of its
  holiness.
- **Restore** the lament — Vazha's nature-mourning — so the elegy endures as written.

This keeps the poem inviolate while still giving the player a real, active role: *the keeper of
what it means.*

---

## 3. The Distortion

**The Aspect of Vengeance (ჩრდილი / Aspect).** The feud itself grown monstrous — *not* the temi,
not the Kists, not any person. It rises from the spilled blood and reaches for the memory, to
drown the guest-law in the noise of old blood. Warded, not slaughtered; victory is that the tale
is kept as Vazha wrote it.

---

## 4. Sensitivity — binding (extends COSMOLOGY §6)

This tale crosses faith and ethnicity, so the rules are strict and they *are the poem's own point*:

1. **Both peoples and both faiths are honored.** Jokola the **Muslim Kist** is the moral hero of
   hospitality; Zviadauri the **Christian Khevsur** dies with noble dignity. Neither people, and
   neither faith, is the villain. The temi are not monsters — they are a grieving community the
   feud speaks through. The *only* antagonist is Vengeance itself (the Aspect).
2. **No people is ranked above another.** Vazha's humanism — *the enemy, too, is a human being* —
   is the canon we protect; any framing that makes one side "bad" is a failure of the tale.
3. **`სტუმარი ღვთისაა`** ("the guest is God's") is used as the real, beloved Caucasian/Georgian
   proverb it is — a cultural-sacred principle, not a theological assertion by the game.
4. **The killing is witnessed, not staged for thrill.** Restraint and grief, never spectacle (see
   ART_DIRECTION §7, SOUND_DIRECTION §6: this beat is near-silent; let it be heavy).

---

## 5. Beat graph

Saga **SAGA_STUMARMASPINDZELI** — *„სტუმარ-მასპინძელი / Host and Guest"* (WORD, home era 4).
Tale **TALE_HOST_AND_GUEST** (TRUE_TALE; **no era unlock** — it restores the Codex + Living
Timeline but gates no historical era). Resonance (ჟღერადობა): **PLACE = mountain** AND
**STATE = STORM** (no ERA gate — a timeless highland memory reachable from any era).

```
THE_MIST_ON_THE_MOUNTAIN (Witness)   ← სტუმარი ღვთისაა: the guest-law invoked
        │
THE_GUEST_TABLE (Aid)                ← help Aghaza lay the sacred guest-table
        │
CHOOSE_WHOSE_EYES (Choose) ─"jokola"─> THROUGH_JOKOLA (Witness) ─┐  ← the host holds the law against his own blood
        └────────────────"aghaza"─> THROUGH_AGHAZA (Witness) ─┤  ← the conscience that sees the man, not the enemy
                                                               ▼
THE_TEMI_COMES (Witness)             ← the community takes the guest; you cannot stop it
        │
WARD_THE_MEANING (Ward)              ← the Aspect of Vengeance; ward the truth, not the life
        │
THE_LAMENT_OF_THE_ARAGVI (Restore)   ← Vazha's nature-mourning; re-light the elegy
        │
IT_IS_MOURNED_AND_REMEMBERED (Witness, terminal)   ← kept as written: a lament, not a triumph
```

The **CHOOSE** is a True-Tale choice (both branches true): witness the tragedy through Jokola's
sacred stand or Aghaza's grieving conscience — they rejoin at `THE_TEMI_COMES`.

---

## 6. Georgian text (primary intent)

Confirmed-safe Georgian woven into the narration: the title **სტუმარ-მასპინძელი**, the proverb
**სტუმარი ღვთისაა**, the character names **ჯოყოლა / ზვიადაური / აღაზა**, **თემი** (the community),
**ხევსურეთის არაგვი** (the Aragvi of Khevsureti).

> **TODO (with the human director):** verify/commission the **exact Vazha-Pshavela verse** for the
> sacred beats before this is treated as final canon. The JSON deliberately uses faithful
> *paraphrase* + the safe proverb rather than fabricated quotations — per the source-honesty rule,
> we do not invent verse. The full ქართული text is the next pass.

---

## 7. Build / import notes (for a backend session)

- An E13 **demo import** of „სტუმარ-მასპინძელი" already exists in the running DB (per CLAUDE.md),
  resonating at `mountain` + `STORM`. This authored version uses **fresh codes**
  (`SAGA_STUMARMASPINDZELI` / `TALE_HOST_AND_GUEST`); if the demo used colliding codes, **retire
  the demo row** so two Heralds don't resonate at the storm-mountain. This authored version is the
  canon one (full beat graph, Distortion, sensitivity-correct).
- `unlocksEraId` is **null** (intentional): the poem gates no historical era; completion still
  grants the Codex entry + one Living-Timeline restoration. If the import/engine requires a
  non-null `unlocksEraId`, flag it — the design intent is *no era unlock*.
- `saga.eraId = 4` (RUSTAVELI / Myth-Weave of the Word) is the literary *home shelf*; the
  **absence of an ERA trigger** is what makes it reachable from any era at a storm-mountain.
- Verifies the proven live path: import → stand on a `mountain` in a `STORM` → Herald →
  enter → Witness/Aid → Choose → Witness → Ward → Restore → complete → Codex + timeline tick.
