# Sound Direction — *Watcher* (მცველი)

> The sonic soul. For the client/audio work. Rests on [COSMOLOGY.md](COSMOLOGY.md).
> Companion: [ART_DIRECTION.md](ART_DIRECTION.md). Hard law it serves: **polyphony is the
> identity of belonging; the sound of restoration is the world remembering its own voice.**

---

## 1. The one sonic idea

**Georgian polyphony is the soul of this game's sound.** Three-voice polyphony (UNESCO-recognized)
is the sonic DNA of Georgian identity — and its structure *is* our cosmology, ready-made:

- A lead voice (**მთქმელი / mtkmeli**) sings alone — a single thread of memory.
- The **ბანი / bani** — the held bass drone — returns beneath it: the ground of remembrance.
- The chorus **answers** in harmony: the community remembering *together.*

So **the "restoration" motif is built from polyphony itself:** a lone voice, the bani rising under
it, then the chorus answering. That is the sound of a memory being kept — and of the Living
Timeline / Sun-Thread relighting. The whole server's progress can be scored as the choir filling in.

---

## 2. Instruments (authentically Georgian)

Foreground the distinctly Georgian palette — accuracy over generic "ethnic" sound:

- **ფანდური (panduri)** & **ჩონგური (chonguri)** — the folk lutes; the everyday, the road, the village.
- **ჩანგი (changi)** — the Svan harp; the ancient, the mythic, high-mountain memory.
- **სალამური (salamuri)** — flute; solitude, shepherd's distance, Witness beats.
- **დოლი (doli)** & **დაირა** — frame drums; movement, Ward/tension, festival.
- **ჭიბონი (chiboni)** (Adjaran bagpipe), **ლარჭემი/სოინარი** (panpipes, Samegrelo), **ბუკი/საყვირი**
  (horns) — regional and ceremonial color, sieges and World Echoes.
- **changi/strings + drone** for the mythic interior eras.

Use real ensembles' sound as the north star (the user's reference: **Kvinteti Urmuli /
კვინტეტი ურმული**). The village voice is *theirs*, not a synth pad.

---

## 3. Villages & towns — the L2 social buzz, sung in Georgian

Towns and villages in the shared History Layer are scored with **authentic regional folk song**,
diegetic where possible (you *hear* people singing in the square — the inhabited-city feeling L2
had with its private stores, but Georgian):

- **Kakhetian** long-table **supra** polyphony (*Mravalzhamier*, *Chakrulo* lineage) — feast, plenty,
  the eastern lowlands.
- **Gurian** **კრიმანჭული (krimanchuli)** — the virtuosic yodel; bright, western, dazzling.
- **Svan** ancient hymns (e.g. *ლილე / Lile*, the sun hymn) — archaic, high, sacred-mountain.
- **Work & road songs** — and especially **ურმული (urmuli)**, the slow song of the ox-cart driver
  on the road. The reference ensemble is *named* for it, and it is **perfect for travel ambience in
  a world crossed on foot** (our L2 "journey is content" pillar). The road should sing.

Regional towns get regional voices, so travel *sounds* like crossing real Georgia.

---

## 4. Per-era ambient palette

- **PARNAVAZ:** sparse, archaic — lone panduri, salamuri on the wind, low drone; pre-polyphony
  emerging, mirroring a script being born.
- **EARLY_KING:** polyphony enters; the first restrained sacred tones as Christianity arrives.
- **GOLDEN_AGE:** fullest, richest polyphony + changi + courtly color; the choir at its height.
- **RUSTAVELI / Myth-Weave:** dreamlike, drone-led, changi and distant voices; the poem's interior.

---

## 5. Scoring the loop (per Interaction)

- **Witness:** near-silence or a *single* voice / salamuri. Let the player *be present.* The most
  sacred beats are almost unscored — restraint is reverence.
- **Aid:** gentle, forward folk motion — panduri, light doli.
- **Ward (combat):** rhythmic doli and tension — but it **resolves to harmony, not a victory
  fanfare.** Winning *sounds like the chorus answering and the mist withdrawing*, never a kill-jingle.
- **Choose:** a held, open chord — two true paths suspended.
- **Restore:** the **restoration motif in full** — lone voice → bani → chorus. The peak sonic reward.

World Echoes (public) and sieges scale this up: many real voices, horns, doli — the communal high.

---

## 6. Sacred restraint (a binding sensitivity rule)

**Georgian Orthodox liturgical chant (საგალობელი — the three chant schools) is sacred.** Use it
**only** in Christian-era sacred moments (Nino, Svetitskhoveli, holy memory), with reverence and
ideally with proper sourcing/performers. **Never** as generic background, **never** in combat,
**never** distorted or remixed for a "boss." Likewise, pagan-era myth gets its *own* archaic sound
(Svan hymn, drone, changi) — honored as that age's voice, never mocked. This mirrors
[COSMOLOGY.md](COSMOLOGY.md) §6: we depict belief with dignity; we never cheapen it.

---

## 7. One line for the client/audio engineer

Build a small **adaptive layer** keyed to era + Interaction + restoration-state, drawing on a
library of authentic Georgian folk (and, for sacred beats only, properly-sourced chant). The
*content* of each cue can be authored/swapped as data later; the *system* just needs the hooks:
`era`, `interactionType`, `isRestoring`, `inTown`, `inEcho`. Polyphony in, museum out.
