# Art Direction — *Watcher* (მცველი)

> The visual soul. For the client session and any UI/asset work. Rests on
> [COSMOLOGY.md](COSMOLOGY.md). Companion: [SOUND_DIRECTION.md](SOUND_DIRECTION.md).
> Hard law it serves: **authenticity over fantasy pastiche; belonging, not conquest;
> homecoming, not museum.** Georgian players must feel the floor of their own inheritance.

---

## 1. The one visual idea

**Memory is light; forgetting is mist; restoration is light returning.** Every visual choice
serves that. An un-restored place is dimmed, desaturated, veiled in **ნისლი (Nisli)** — a pale
grey-blue mist clinging to edges, faces worn smooth, ornament unraveling. When the Watcher
restores a memory, **warm gold light returns**, faces resolve, ornament re-knits, and a fine
**gold thread** (the Sun-Thread) draws taut through the scene. The player should *see* the act
of remembering.

We never reach for horror, gore, or demonic spectacle. The enemy of this world is *absence*,
not monsters. That restraint is the whole aesthetic.

---

## 2. Palette

Rooted in real Georgian material culture — church fresco, **მინანქარი (minankari)** cloisonné
enamel, stone towers, manuscript gold.

**Core (the restored world):**
- **Fresco gold / heat-gold** `#C9A227` — restoration, the Sun-Thread, illuminated initials, sacred light.
- **Cobalt / lapis** `#1F3A6E` — the deep blue of Georgian frescoes and minankari; night, depth, the Word strand.
- **Oxblood / wine** `#7B2226` — Georgian wine, martyr-red, the History strand, banners.
- **Enamel turquoise** `#2E8B8B` — minankari highlight, accents, UI active states.
- **Mountain stone / bone** `#D8D2C4` and **basalt** `#2B2A28` — Svaneti/Mtskheta stone, parchment, base neutrals.

**The fraying (Nisli / Mivitsqeba):** desaturate toward `#8A93A0` grey-blue; lower contrast;
drain the golds. This is a *treatment*, not a color — applied as an overlay to un-restored zones,
frayed memories, and Distortion fields.

**Strand tints** (subtle, for UI/codex categorization): History = oxblood, Myth = forest/stone
green `#3E5641`, Word = lapis.

---

## 3. Script & ornament — the theme *is* the typography

Georgian letterforms are not decoration here; **the birth of the script is the founding myth**
(see *The First Crown*). The three historical hands carry meaning:

- **ასომთავრული (Asomtavruli)** — ancient monumental majuscule. Use for **sacred headers,
  era titles, inscriptions, the most solemn moments.** Carved-in-stone feeling.
- **ნუსხური (Nuskhuri)** — ecclesiastical minuscule. Use in the **Codex/Matiane** and
  Christian-era sacred text, paired with asomtavruli initials (the real *khutsuri* manuscript pairing).
- **მხედრული (Mkhedruli)** — the modern hand. **Body text, UI, dialogue, the real verse.**

**Ornament vocabulary:** the **ბორჯღალი (Borjgali)** seven-arm sun (continuance, the Living
Timeline node), the **Bolnisi cross** (Christian-era framing), grapevine scroll (Nino), minankari
enamel borders, manuscript marginalia, the **asomtavruli letter as a decorative glyph**. Frame UI
panels like illuminated manuscript pages, not sci-fi HUDs.

> **i18n-first / Georgian primary:** every layout is designed for ქართული text *first*, Latin
> second. Pick fonts with full Georgian coverage (e.g. **BPG** families, *Noto Sans/Serif
> Georgian*). Never let Georgian be the afterthought that breaks the layout.

---

## 4. The Codex / Matiane (the retention & soul hook)

The single most important screen. It is an **illuminated Gospel codex** (reference: Adishi,
Jruchi, Gelati gospels) — parchment ground, gold leaf initials, asomtavruli/nuskhuri, marginalia.
Each restored tale becomes **one illuminated page**: the real ქართული verse/chronicle primary, a
translation secondary, a piece of art, the history note, the music motif. Turning the pages should
feel like holding a relic the Watcher personally carried back. Un-restored entries are **faded,
mist-veiled silhouettes** that *illuminate* on completion — the player watches their own Matiane
light up, mirroring the server's Living Timeline.

---

## 5. Resonance Points, Heralds, and entering a Thread

- **Resonance Point (ჟღერადობა):** a place where the world *thins*. Visually: a faint gold thread
  in the air, slow ambient motes, a localized clearing of Nisli, a subtle shimmer — *not* a yellow
  exclamation mark. It reads as *a memory waiting*, discovered, not assigned.
- **The Herald (მახარობელი):** a half-lit figure — the first line of the story given form. Appears
  *of* the place (the stranger on the mountain), never a quest-board NPC.
- **Entering a Thread:** the open world **folds inward** — edges draw toward a gold seam, the
  ambient world hushes, and the staged memory opens. Leaving restores the seam to the world.

---

## 6. Eras — distinct visual identities

Each era is a different light and architecture (helps the seamless world still feel *traveled*):

- **PARNAVAZ — The Age of Parnavaz:** early Iberia. Bronze, raw stone, pagan idols (Armazi),
  Armaztsikhe's walls. Pre-script-to-script: ornament sparse, becoming inscribed.
- **EARLY_KING — The Early Kingdoms:** early Christian Kartli. Mtskheta, Svetitskhoveli, first
  frescoes, the grapevine cross. Light entering — golds warm up.
- **GOLDEN_AGE — The Golden Age:** Tamar's era. Vardzia cave-city, opulent fresco and minankari,
  illuminated manuscripts at their height. Richest palette.
- **RUSTAVELI — The Myth-Weave of the Word:** the dreamlike interior of the poem. Panther imagery,
  Kajeti, a world half-real — the most stylized, least literal era.

---

## 7. Distortions — design language (no Hollywood)

Distortions are **erasure made visible**, never demon-horror and **never the beloved figure**:

- **გამოძახილი (Echo):** a translucent, looping after-image of a force — a siege that repeats, a
  storm-shape with no body.
- **ჩრდილი / Aspect:** a single quality grown monstrous, rendered abstractly — the *Aspect of the
  Foreign Yoke* as a crushing weight of grey ornament; faces **worn smooth** (the forgotten).
- **ქაჯი (Kaji):** the sorcerous falsifier — masked, woven of **false, inverted letters** and
  twisted script (memory rewritten into a lie). The most "designed" enemy; still symbolic.

Combat is **Ward** — defensive, restrained. **No damage numbers flying, no loot explosions, no
kill-counters.** Victory feedback = the mist withdraws, color and ornament return, the chorus
answers (see Sound). The reward is *the memory held true.*

---

## 8. UI principles (binding for the client)

1. **Manuscript, not HUD.** Ornamental frames from minankari/marginalia; gold-on-dark; generous quiet.
2. **The five Interactions get five clear visual verbs** — Witness (an opening eye / held gaze),
   Aid (an offered hand), Ward (a stance/shield), Choose (a fork of light), Restore (a relighting).
   The client maps the `interaction` enum to these; it never hard-codes a tale.
3. **Content-agnostic rendering.** UI shows whatever the backend serves (`narration`, `interaction`,
   beat graph). Authenticity lives in *style*, never in baked-in story.
4. **Restraint as reverence.** Sacred/Witness beats are near-empty of UI — let the player *be present.*
5. **Belonging, not conquest.** Reward and progress feedback is light returning and pages
   illuminating, never numbers going up.
