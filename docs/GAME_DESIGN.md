# GAME DESIGN — "Watcher" (working title)

> The canonical product & story-system design. This is the *what* and *why*.
> For the *how* (architecture, build roadmap, working agreement) see
> [BUILD_PROMPT.md](BUILD_PROMPT.md). This document is content-agnostic on purpose:
> it defines the **systems**, never the specific stories. Stories are authored as data.

---

## 0. The dream, in one breath

A backend-first Georgian historical-fantasy MMO where the player is a **Watcher** — a
soul that does not die, passing through the eras of Georgian history. The Watcher does not
rewrite history or slay its heroes. The Watcher **remembers**. Where memory has frayed —
a battle half-forgotten, a myth gone silent, a poem whose meaning is being twisted — the
Watcher walks into it, witnesses it truthfully, protects it from distortion, and so
**restores it to the world**.

History, myth, and literature are not separate menus. They are three strands of one living
memory, and the player's job — alone and together with thousands of others — is to keep
that memory whole.

The emotional target is specific: a Georgian player should feel the floor of their own
inheritance under their feet. Not a museum. A homecoming.

---

## 1. The three strands of memory

Every piece of content in the game is one of three **Strands**, but all three are modeled
by the *same* system (see §3). This is the central design guarantee.

- **History (ისტორია)** — what happened. Foundings, kings and queens, battles, golden
  ages, betrayals, rebirths. From Parnavaz onward across the whole timeline.
- **Myth (მითი)** — what was believed. Folk legend, creatures, deities and devis, the
  sacred geography of mountains and springs, the oral memory beneath the written one.
- **Word (სიტყვა)** — what was written. The literature: ვეფხისტყაოსანი, სტუმარ-მასპინძელი,
  and the vast body of Georgian poems and prose. Each work is a body of living memory the
  Watcher can step inside.

Because all three share one structure, a chronicle, a legend, and a poem are the *same kind
of object* to the engine. That is what lets one backend hold all of it — and lets us keep
adding content forever without touching code.

---

## 2. The core loop — the mechanic you described, generalized

You described it perfectly with the mountain. Here is that exact moment, named and made
universal so it works for *any* tale:

1. **The Watcher moves through the persistent shared world** (the History Layer), divided
   into **Eras**. Travel between eras is backend-gated; you go where memory has opened to you.
2. Scattered through the world are **Resonance Points** — places, times, or conditions where
   a memory is thin and waiting. The mountain on a storm day is a Resonance Point for
   სტუმარ-მასპინძელი. A Resonance Point can be a *place*, a *weather/time state*, an *era*,
   an *item you carry*, a *prior tale you've restored*, or any combination.
3. When the Watcher meets a Resonance Point's condition, its **Herald** appears — the NPC
   that is the living doorway into the tale (the stranger met on the mountain). The Herald
   is not a quest-giver with a yellow exclamation mark; it is the *first line of the story*
   given form.
4. The Watcher accepts. The world **folds inward** into a **Thread** — a staged, self-
   contained telling of that tale, which the Watcher experiences from inside.
5. The Thread plays out as a sequence of **Beats** (scenes). At chosen moments the Watcher
   has **Interaction Slots** — defined ways to *act inside the story without overwriting it*
   (see §4).
6. Where the memory is being corrupted, a **Distortion** manifests — a boss-form, an Aspect
   or Echo, never the hero themselves. The Watcher confronts the Distortion to keep the tale
   true.
7. The Thread resolves. The Watcher earns **rewards**, the tale is added to their personal
   **Codex of memory**, the **Living Timeline** advances (§6), and new Resonance Points,
   eras, or Threads **unlock**.

```
shared world → reach a Resonance Point → Herald appears → enter Thread →
   live the Beats → make Interactions → cleanse the Distortion → restore the tale →
   reward + Codex + Timeline progress → new memory opens
```

This is one loop. It is the loop for a battle, a legend, and a poem alike.

---

## 3. The universal container: Saga → Thread → Beat

This is the heart of the system. **Everything authored is a graph of these, stored as data.**

- **Saga** — a whole body of memory. A king's reign. A myth cycle. *An entire book.*
  ვეფხისტყაოსანი is a Saga; სტუმარ-მასპინძელი is a (smaller) Saga; "The Founding under
  Parnavaz" is a Saga. A Saga groups Threads and defines its own unlock order.
- **Thread** — one self-contained narrative experience the Watcher enters and completes in a
  sitting (solo, party, or — for the great set-pieces — raid). A chapter of a poem, a single
  legend, one decisive battle. This is the unit that the mountain-encounter opens.
- **Beat** — an ordered scene inside a Thread. Beats form a **graph**, not just a line:
  they can branch and rejoin, so any narrative shape fits (linear poem, branching legend,
  multi-front battle).
- **Interaction Slot** — attached to a Beat: what the Watcher may *do* here (§4).
- **Trigger / Resonance** — the condition(s) that open a Saga, Thread, or Beat (§5).
- **Distortion** — an encounter (the "boss") bound to a Beat, expressed generically (§8).
- **Outcome** — branch results, what is restored, what unlocks next.
- **Reward / Unlock** — granted on outcomes.

**The design guarantee:** because a Thread is a *typed graph of Beats with typed Triggers,
Interactions, Distortions, and Outcomes*, any Georgian story — historical, mythic, or
literary, of any shape — can be expressed as data in this schema. No story needs new Java.
Authoring a new poem = inserting rows, never writing engine code. This is the literal
meaning of our project law: **systems are code, stories are data.**

> Reconciliation with earlier vocabulary: the old Act / Chapter / Step / Objective maps to
> **Saga / Thread / Beat / Interaction-and-Trigger**. Same idea, richer and graph-shaped.

---

## 4. How the Watcher acts inside a story *without disrespecting it*

This is the moral and creative spine of the game, and it is also a clean system. The Watcher
never replaces the hero, never "kills Rustaveli's characters." Instead, every Beat exposes
one or more **Interaction Slots** drawn from a fixed, generic vocabulary:

- **Witness** — be present; the act of true seeing itself stabilizes the memory. (Passive,
  cinematic, sometimes the *entire* point — the most sacred Beats are pure witness.)
- **Aid** — help a character do what they were always going to do (carry the wounded,
  light the fire, find the path, deliver the word).
- **Ward** — stand between the tale and its corruption: protect a character or place from a
  Distortion. This is where combat lives, and it is always *defensive of the story*.
- **Choose** — at branch Beats, decide which true thread of memory to follow (for tales with
  genuine variants, or for clearly-marked "what-if" Echoes — see below).
- **Restore** — a ritual/puzzle/attunement act: re-light a memory, reassemble a broken
  verse, realign a sacred place.

Two tiers of Thread protect canon while giving infinite replay:

- **True Threads (canon)** — the goal is to keep the tale *as it was*. Distortions try to
  bend it; you hold it true. Restoring a True Thread is the "real" story.
- **Echo Threads (what-if / mythic distortion)** — explicitly framed as memory gone wild:
  corrupted reflections, dreams, "what the silence imagined." Here variation, alternate
  outcomes, and harder boss-forms are allowed *because the fiction marks them as not-real*.
  This is how a single poem yields endlessly repeatable MMO content without ever disrespecting
  the source.

---

## 5. Resonance — how tales find the player (generic trigger system)

A **Resonance Point** is just an evaluated condition bound to a location/anchor in the world.
The trigger vocabulary is generic and composable (AND/OR of conditions):

- **Place** — within radius of a world anchor (the mountain).
- **State** — a world/environment state is active (storm, night, a festival, an era event).
- **Era** — the Watcher is in / has unlocked a given era.
- **Carry** — the Watcher holds a relic/token (often earned from a related tale).
- **Memory** — a prior Saga/Thread has been restored (story prerequisites).
- **Company** — solo / party / raid size conditions.
- **Time** — real or in-game time windows (seasonal/world-event tales).

The same engine that evaluates "may this Thread open?" also evaluates "is this Beat's
objective met?" and "may the Watcher enter this Era?". One condition engine, reused
everywhere. The mountain-on-a-storm-day is simply `Place(mountain) AND State(storm)`.

---

## 6. Making it a *real* MMO (and a beloved one)

Story intimacy + massively-shared world. Both, deliberately:

- **The shared History Layer** is persistent and social: players roam eras together, meet at
  Resonance Points, see each other's banners, form parties.
- **Personal & party Threads** — most tales fold into an instance for you or your party, so
  the narrative stays intimate and paced. The Watcher's story is *yours*.
- **World Echoes (public memory)** — the great moments (a famous battle, a Golden-Age
  set-piece) periodically **manifest in the open world** as public, server-shared events that
  many players converge on — the MMO communal high. These are Threads flagged `public`.
- **The Living Timeline (server meta-progression)** — every restored tale contributes to a
  **server-wide restoration of Georgian memory**. The community, together, re-illuminates the
  timeline era by era. Personal progress *and* a shared monument the whole server builds.
- **The Codex of Memory** — each Watcher keeps a growing personal archive of every tale they
  have restored: the verse, the art, the music, the history note. This is the retention and
  the *soul* hook — a player's Codex is the story of Georgia they personally carried back.
- **Social systems** — parties, guilds-as-"orders/circles of Watchers," shared Resonance
  discovery, co-op Restore rituals that need multiple players.

### What makes Georgian players feel it (art direction, system-level)

These are design constraints, not stories:

- **Georgian is first-class.** The original ქართული text of every poem/chronicle is primary;
  the engine is built i18n-first so the source language is never an afterthought. The Codex
  shows the real verse.
- **Authenticity over fantasy pastiche.** Real geography, real ornament (asomtavruli /
  nuskhuri / mkhedruli letterforms, architecture, polyphony as the sonic identity). Myth is
  rendered with reverence, not Hollywooded.
- **The feeling is custodianship, not conquest.** The dominant verb is *remember / restore /
  protect*, not *loot / dominate*. Power fantasy is replaced by **belonging** fantasy.
- **Collective re-illumination.** Watching the server-wide timeline light up over weeks is a
  shared act of cultural memory. That is the awakening you want.

---

## 7. The World Feeling — our Lineage 2 doctrine (north star)

> The single most important reference for *how the world should feel*: **Lineage 2** — **not**
> WoW, **not** Aion. We are chasing the L2 social-world soul. This is a hard design constraint
> and it shapes the **backend architecture from day one**, not just the art.

What made the L2 world feel alive — restated as enforceable pillars:

1. **One seamless, persistent, shared world — a home, not a lobby.** The History Layer is
   where life happens: hunting grounds, towns buzzing with people, travel *through* the land.
   We minimize instancing. **Reconciliation with our narrative system:** Threads (instanced
   tales) are sacred, intimate *dives* — the exception, not the daily loop. The dominant,
   default experience is the open shared world. We reject WoW-style phasing,
   instance-everything, and teleport-everywhere convenience.
2. **Interdependence over convenience — you genuinely need other players.** Roles complete
   each other (our reframe of the buffer / healer / tank / damage / utility weave). Soloing is
   possible but the world *rewards the party*. Community formed in L2 out of necessity — and
   that necessity is exactly what forged the friendships. We design for it on purpose.
3. **Risk, weight, and consequence.** Death matters. Content is contested. Progression is
   slow and earned, so time spent becomes attachment. No frictionless safety nets that drain
   the world of stakes.
4. **Territory & politics.** Clans and alliances (in-fiction: **Orders / Circles of
   Watchers**), contested **places of power**, and **siege-scale events** over sacred sites
   and the strongest Resonance. Server-level rivalries, alpha clans, and player legends. The
   server grows *its own history* on top of ours.
5. **A living, player-driven social economy.** Player shops in towns (the L2 private-store
   buzz that made cities feel inhabited), gathering and crafting, trade as a **face-to-face
   social act** — not an anonymous auction house. Scarcity + physical trade keep towns alive.
6. **Persistent social identity & reputation.** Your name *means something* on your server.
   Rivalries, alliances, and reputations persist and are remembered.
7. **Atmosphere & the traveled world.** Polyphonic ambient score, a world crossed on foot,
   the journey as content (ties to our authenticity pillar). No fast-travel firehose.
8. **The world is the content** — emergent social play and the open world itself, not a
   quest-hub theme park.

**Open-world Distortions = our raid/world-boss equivalent:** the great contested **World
Echoes** (§6) are the L2 epic-boss moments — server-shared, fought over, the source of server
legend. The **Living Timeline** is the communal server-history layer those acts build.

**Conflict / PvP model — DECIDED: contested-zones + sieges (the model that fits Georgian
history most).** Georgian history *is* fortress history — an unbroken chain of stronghold
defenses and sieges against invaders, and a recurring arc of fragmentation giving way to unity
against a force that would erase the homeland's faith and memory. That maps exactly onto both
L2's legendary siege endgame and our theme. So:

- **Homeland, towns, and ordinary hunting grounds are safe** (no ganking countrymen — that
  would betray the *belonging, not conquest* theme).
- **Places of power, World Echoes, and siege sites are open, consequential PvP** — the
  contested sacred ground.
- **Sieges are the centerpiece** — clans/Orders fight over strongholds and places of power,
  the most Georgian large-scale conflict there is, and the L2-soul set-piece.

In-fiction, conflict is the **memory-war**: not all Watchers agree how memory should be kept.
**Restorers** hold it true; **Revisionists** — the distortion-touched — would bend it. PvP
carries L2-grade consequence (karma/reputation), but is always *over ground and memory*, never
idle slaughter. The backend should keep the model dial-able (the safe/contested boundary is
data), but contested-zones-+-sieges is the design target.

**Backend implications (build-time, since we are backend-first):** this pillar must not be
retrofitted — the architecture must allow it from the start, even if features ship later:
seamless world → spatial partitioning + **area-of-interest / interest management** and efficient
world snapshots (channels/shards only if forced, and minimized); durable **world & social
persistence**; **server-authoritative player economy** (trade, player shops, crafting);
**clans / alliances / sieges** as social + territorial state with scheduled large-scale events;
**contested spawns / world bosses** with fair shared spawn authority and anti-grief; persistent
**reputation / identity** records; server-authoritative **risk/death rules**. See E14 in the
roadmap ([BUILD_PROMPT.md](BUILD_PROMPT.md)).

---

## 8. Distortions — bosses without disrespect (generic)

A **Distortion** is a generic encounter bound to a Beat. Forms (all data-authored):

- **Echo** — a hollow repetition of a force/event (siege that will not end, a storm that
  remembers a tragedy).
- **Aspect** — a single quality torn loose and grown monstrous (the Aspect of Betrayal, the
  Aspect of the Panther's grief) — symbolic, never the literal beloved character.
- **Distortion proper** — the memory actively being falsified; defeating it = restoring truth.

Encounters are defined by data (phases, the Beat they guard, the Interaction the Watcher uses
to overcome them — often **Ward** or **Restore**, not just damage). The reward of victory is
narrative: the tale is held true.

---

## 9. From fiction to backend (system map)

Each fiction concept above is a generic backend engine + data, never hard-coded story:

| Fiction concept            | Backend engine (code)          | Authored as data |
|----------------------------|--------------------------------|------------------|
| Strands / Sagas / Threads / Beats | **Narrative/Content registry** | Saga, Thread, Beat rows (graph) |
| Resonance Points / Heralds | **Trigger/Resonance engine**   | Trigger condition rows + world anchors |
| Entering a Thread          | **Instance engine**            | Thread instance config |
| Beats & Interaction Slots  | **Objective/Interaction evaluator** | Beat + Interaction rows |
| Distortions (bosses)       | **Encounter engine**           | Distortion/encounter rows |
| Outcomes / branches        | **Outcome engine**             | Outcome + branch rows |
| Rewards / unlocks          | **Reward & Unlock engine**     | Reward rows |
| Eras & era-gates           | **World/Timeline engine**      | Era + gate rows |
| Living Timeline (server)   | **Progression engine**         | Server/era progress state |
| Codex                      | **Codex/Archive service**      | Player memory records |
| Parties / public Echoes    | **Party/Matchmaking engine**   | Party + public-Thread config |
| Seamless shared world (L2) | **World/interest-management engine** | Zone/anchor + spawn config |
| Player economy / shops     | **Economy & trade service**    | Items, recipes, shop listings |
| Clans / alliances / sieges | **Social & territory engine**  | Clan, alliance, siege/event config |
| Reputation / identity      | **Reputation service**         | Player social/reputation records |

Core data entities (conceptual — real schema via migrations during build):

- `era`, `era_gate`
- `saga`, `thread`, `beat`, `beat_edge` (the narrative graph)
- `interaction_slot` (type: witness/aid/ward/choose/restore, bound to a beat)
- `trigger` (composable conditions; reused for resonance, objectives, era-gates)
- `world_anchor` (places/states a trigger references)
- `distortion` / `encounter` (bound to beats)
- `outcome`, `outcome_unlock`, `reward`
- `watcher` (player soul), `watcher_codex_entry`, `watcher_progress`
- `party`, `instance` (a live Thread run)
- `timeline_progress` (server-wide restoration state)
- `clan`, `alliance`, `siege_event`, `place_of_power` (L2 territory & politics)
- `item`, `recipe`, `shop_listing`, `trade` (player-driven social economy)
- `reputation` (persistent server social identity)

Everything else (the actual mountain, the actual poem, the actual battle) is **content rows
+ localized text**, authored later through seed SQL → JSON/YAML import → admin panel
(the E13 content pipeline). The engines never know which story they're running.

---

## 10. Why this fits *every* Georgian story (the closing argument)

- A **chronicle** (Parnavaz founding) = a Saga of True Threads with Witness/Aid/Ward Beats
  and a Distortion of the force that threatened the founding.
- A **legend** = a Saga of myth-strand Threads, heavier on Restore and Echo Threads.
- A **poem/book** (სტუმარ-მასპინძელი, ვეფხისტყაოსანი, any of the hundreds more) = a Saga
  whose Threads are its chapters, opened by Resonance Points placed where the work lives,
  experienced as Witness with Ward against Distortions, its real verse preserved in the Codex.

Three strands, one structure, infinite content, zero engine rewrites, total respect. That is
the system. Now we build the backend that runs it.
```
