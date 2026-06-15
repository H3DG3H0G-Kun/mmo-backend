# Saga — ვეფხისტყაოსანი / The Knight in the Panther's Skin

> Strand **WORD** · era **RUSTAVELI** (4) · the great multi-Tale Saga. Design doc for the whole
> arc. Data: [content/the-knight-in-the-panthers-skin.json](../../../content/the-knight-in-the-panthers-skin.json)
> (one saga, **five Tales**, one import). Rests on [../COSMOLOGY.md](../COSMOLOGY.md). Source:
> **შოთა რუსთაველი, ვეფხისტყაოსანი** (~12th c.), dedicated to King Tamar.

---

## 1. Why a whole Saga (and how it proves the architecture)

ვეფხისტყაოსანი is the summit of the Georgian Word, and it is the cleanest possible proof of
"systems are code, stories are data": **one Saga, many Tales, chained by `PRIOR_TALE`, zero engine
changes.** The opening Tale resonates at the Golden-Age court; each later Tale opens because you
restored the one before it, so the epic unfolds as a true sequence the Watcher carries. It also
closes the cosmology's central loop — the **ქაჯნი (Kajni)** that the Watcher fights across the
whole game first appear, in fiction, here in their own home of **Kajeti**.

## 2. The five Tales (the arc)

| # | Tale code | title | opens when | heart |
|---|-----------|-------|------------|-------|
| 1 | `TALE_PANTHERS_SKIN_OPENING` | The Opening | `ERA=RUSTAVELI`+`PLACE=court` | Rostevan's court, Tinatin crowned, the weeping panther-knight, Avtandil's oath |
| 2 | `TALE_TARIELS_TALE` | Tariel's Tale | `PRIOR_TALE=…OPENING` | the cave & Asmat; India; Nestan-Darejan; the slain suitor; her casting-away; the grief |
| 3 | `TALE_THE_BROTHERHOOD` | The Sworn Brothers | `PRIOR_TALE=TARIELS_TALE` | Avtandil's return, the oath of brotherhood, Pridon the third — *megobroba* |
| 4 | `TALE_THE_SEARCH_FOR_NESTAN` | The Search | `PRIOR_TALE=THE_BROTHERHOOD` | Gulansharo, Phatman, the word that Nestan is held in Kajeti |
| 5 | `TALE_THE_TAKING_OF_KAJETI` | The Taking of Kajeti | `PRIOR_TALE=THE_SEARCH_FOR_NESTAN` | the three brothers storm the Kaji fortress, free Nestan, and the three crowns end in concord |

## 3. The Distortions (per Tale)

- Tale 2 — **the Aspect of Grief / the Panther's grief**: the despair that would swallow Tariel and
  bury his loss in silence. Warded, not slain (Tariel is the beloved figure; his *grief* is the foe).
- Tale 3 — **the Aspect of Despair/Division** that would break the brotherhood before it forms.
- Tale 4 — **the Kajni falsifying the trail**, erasing where Nestan is held.
- Tale 5 — **the Kajni and the host of Kajeti** themselves: here, and only here, the cosmology's
  falsifiers stand in their own fortress, and the Ward is the epic's climax. Freeing Nestan is a
  **RESTORE** — love kept is never lost.

The beloved figures (Tariel, Nestan, Avtandil, Tinatin, Pridon) are **never** bosses; the foes are
the Kajni and abstracted Aspects, exactly per the Distortion law.

## 4. Themes to keep (Rustaveli's soul)

- **სიყვარული / love** (mijnuroba) and **megobroba / dzmadnaphitsoba** — sworn brotherhood — as the
  two forces that move the world; Tale 3's CHOOSE lets the Watcher witness which is the greater
  engine (both true).
- **Generosity** — „რასაცა გასცემ შენია, რაც არა — დაკარგულია" (what you give is yours; what you
  keep is lost) — surfaced in the concord coda.
- **The equality of the queens** — Tinatin and Nestan rule in their own right; Rustaveli's proto-
  humanism is canon to preserve.

## 5. Verse honesty (TODO with the director)

Safe, well-known lines are woven in and marked for verification: „სჯობს სიცოცხლესა ნაძრახსა
სიკვდილი სახელოვანი", „ხამს, მოყვარე მოყვრისათვის…", „რასაცა გასცემ შენია…". **The full ქართული
verse for all five Tales must be verified/sourced before this Saga is final canon** — the JSON uses
faithful paraphrase plus these known phrases, never fabricated quotation.

## 6. Build / import

One package, five Tales, imports as a unit (era 4 exists → **imports now**). Replaces the earlier
standalone `the-panthers-skin-opening.json` (folded in as Tale 1, same code). Future: the weddings
and each kingdom's return can each become their own Tale later; the arc is left able to grow.
