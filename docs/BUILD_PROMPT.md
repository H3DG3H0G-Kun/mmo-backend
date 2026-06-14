# BUILD: Georgian Historical-Fantasy MMO Backend ("Watcher")

You have full access to this repository (`mmo-backend`). You are my engineering partner
building a **backend-first MMO**. Read this whole brief, then work in disciplined,
verifiable slices. Do not dump a giant unreviewable changeset — build vertically,
prove each slice works, then move on.

## 1. The product (context, not code instructions)

> **The full product & story-system design is [GAME_DESIGN.md](GAME_DESIGN.md). Read it
> first — it defines the systems the backend must run.** Summary below.

A Georgian historical-fantasy MMO. The player is a **Watcher** — a soul that does not die,
passing through the **Eras** of Georgian history. The Watcher never rewrites history or slays
its heroes; the Watcher **remembers, witnesses, and restores** memory where it has frayed.

Content spans **three strands, modeled by one system**: **History** (chronicles, kings,
battles, from Parnavaz on), **Myth** (legend, creatures, sacred geography), and **Word**
(the literature — ვეფხისტყაოსანი, სტუმარ-მასპინძელი, and the whole body of Georgian poetry
and prose). A chronicle, a legend, and a poem are the *same kind of object* to the engine.

**The universal container (the whole game in one schema):** `Saga → Thread → Beat`, a typed
**narrative graph** authored entirely as data.
- **Saga** = a body of memory (a reign, a myth cycle, *an entire book*).
- **Thread** = one self-contained tale the Watcher enters (a chapter, a legend, a battle) —
  solo / party / raid.
- **Beat** = an ordered, branchable scene; each Beat exposes **Interaction Slots**.

**The core loop (your mountain mechanic, generalized):** the Watcher roams the shared world →
reaches a **Resonance Point** (a composable condition: place AND/OR state/era/item/prior-tale,
e.g. `Place(mountain) AND State(storm)`) → its **Herald** NPC (the first line of the story
given form) appears → accept → the world folds into a **Thread** → live the **Beats**, acting
only through respectful **Interaction Slots** — *Witness, Aid, Ward, Choose, Restore* (never
replacing the hero) → cleanse any **Distortion** (Echo/Aspect boss-form, never the beloved
character itself) → the tale is restored → **reward + Codex entry + Living Timeline progress +
new unlocks**.

Two Thread tiers keep canon safe with infinite replay: **True Threads** (hold the tale as it
was) and **Echo Threads** (explicitly "what-if"/corrupted memory, where variation and harder
bosses are allowed because the fiction marks them as not-real).

MMO fabric: persistent shared **History Layer** by era (backend-gated travel) · personal/party
Thread instances for intimacy · **World Echoes** (public server-shared Threads = the communal
high) · the **Living Timeline** (server-wide re-illumination of Georgian memory) · the
**Codex of Memory** (each Watcher's growing personal archive of restored tales — the retention
and soul hook). Design constraints that matter: **Georgian text is first-class / i18n-first**,
authenticity over fantasy pastiche, the dominant verb is *remember/restore/protect* (belonging
fantasy, not conquest).

Core loop: `shared world → reach Resonance Point → Herald appears → enter Thread → live Beats
→ Interactions → cleanse Distortion → restore tale → reward + Codex + Timeline → new memory opens`.

## 2. Non-negotiable architecture principles

1. **Systems are code. Stories are data.** Never hard-code a story, tale, boss, era, or
   trigger into Java. Build generic engines — **narrative/content registry** (Saga/Thread/
   Beat graph), **trigger/resonance engine** (composable conditions, reused for resonance
   points, beat objectives, and era-gates), **instance engine**, **objective/interaction
   evaluator** (Witness/Aid/Ward/Choose/Restore), **encounter engine** (Distortions),
   **outcome engine**, **reward & unlock engine**, **world/timeline engine** (eras +
   server-wide Living Timeline), **party/matchmaking engine**, **codex/archive service**.
   Author all content as data (seed SQL now; JSON/YAML import and admin panel later — E13).
   I must be able to add an entire new poem/chronicle/legend — a new Saga with its Threads,
   Beats, Resonance Points, and Distortions — with **zero engine changes**. See the system
   map and entity list in [GAME_DESIGN.md](GAME_DESIGN.md) §9.
2. **Backend is the source of truth.** The client sends *intentions* (move here, attack
   target, enter trial, accept quest, claim reward). The backend **validates and decides**
   (allowed? valid move? quest unlocked? party eligible? instance complete? what reward?).
   No important state is client-authoritative — this must be MMO-/anti-cheat-ready.
3. **The message contract matters more than the transport.** v1 uses REST over HTTP/2 for
   account/meta actions and WebSocket (JSON) for realtime. Commands/events MUST be
   transport-independent so we can later add UDP/ENet/protobuf/FlatBuffers **without
   rewriting game logic**.
4. **The world feeling is Lineage 2 — not WoW, not Aion.** A seamless, persistent, *shared*
   world is the default home; instanced Threads are the exception. Design for **interdependence**
   (you need other players), **risk/consequence**, **territory & politics** (clans, alliances,
   sieges), a **player-driven social economy** (player shops, crafting, face-to-face trade —
   no anonymous auction house), and **persistent social identity**. The architecture must not
   preclude this from day one: plan for **area-of-interest / interest management**, durable
   world & social persistence, and server-authoritative economy/territory. Reject WoW-style
   phasing, instance-everything, and teleport-everywhere convenience. See
   [GAME_DESIGN.md](GAME_DESIGN.md) §7.

## 3. Current tech stack & repo state (verified — don't re-derive)

- Java 25, Spring Boot **3.5.5**, multi-module Maven monorepo (`ge.mmo` group).
- Only module today: `services/auth-service` — a bare `@SpringBootApplication` with web +
  actuator deps and `health,info` endpoints. No controllers, no DB/JPA/Redis/Kafka/security.
- Local infra (Docker Compose) is real and working: PostgreSQL 16, Redis 7, **Kafka 7.6
  still on ZooKeeper**, Kafka UI, Prometheus, Loki, Promtail, Grafana.
- Windows dev scripts work: `dev-up.bat`, `dev-down.bat`, `dev-logs.bat`,
  `dev-reset.bat dev` (resets infra + runs seeds; proven via `mmo_meta.seed_runs`).
- `.github/workflows/ci.yml` exists but is **broken** (malformed "Set up Java" step).
- `contracts/` and `clients/godot-client/` do **not** exist yet.
- Working tree clean, `main` tracks remote.

## 4. Phase 0 — Harden the foundation FIRST (do this before any feature work)

Complete these, each as its own commit, CI green after each:

1. **Fix CI.** Repair `.github/workflows/ci.yml` so `mvn -B -ntp verify` actually runs on
   push/PR to `main` with Temurin Java 25 + maven cache.
2. **Kafka → KRaft.** Remove the `zookeeper` service and convert `kafka` to KRaft mode in
   `docker-compose.yml` (keep ports/env contract stable). Verify `dev-reset.bat dev` still
   comes up clean and Kafka UI connects.
3. **Upgrade Spring Boot to 4.x** (latest stable). Update parent BOM, fix any breaking
   changes, confirm `mvn verify` and the running service are healthy. If 4.x has a hard
   blocker on Java 25, tell me and propose the fallback rather than silently downgrading.
4. **Add monorepo folders:** `contracts/` (with a README describing it will hold OpenAPI +
   WebSocket JSON schemas + protocol versions) and `clients/godot-client/` (placeholder
   README; target Godot 4.5.1 stable). Empty-but-structured is fine.

Stop after Phase 0 and give me a short status + how you verified each item before starting Phase 1.

## 5. Phase roadmap (build in order; each phase = a vertical, demoable slice)

- **E06 — MMO Foundation Slice.** The first real end-to-end path:
  `login → create Watcher → spawn into world → party → reach Resonance Point → Herald → enter
  Thread → complete a Beat → reward`. REST for auth/character/profile; WebSocket for session
  handshake, world entry, movement, world snapshots, party updates, Thread/Beat + reward events.
  This is where real feature dev begins.
- **E07 — Narrative Engine v1 (Saga/Thread/Beat).** The universal container as generic engines:
  Saga/Thread/Beat graph + Interaction Slots (Witness/Aid/Ward/Choose/Restore) + the composable
  Trigger/Resonance engine + Outcome + Reward/Unlock + Codex. Objective/interaction evaluator.
  Seed content: **one Saga, one Thread, opened by one Resonance Point** (a placeholder tale —
  no real story authored yet). Nothing story-specific in Java.
- **E08 — Instance/Party/Matchmaking v1.** Thread instances (solo/party; raid later for the
  great set-pieces), join/leave, completion events, and **public World Echo** Threads.
- **E09 — World/Timeline v1.** Eras + era-gates (history layers, unlock validation, era travel)
  and the **server-wide Living Timeline** progression.
- **E10 — Godot Client v1.** auth UI, WebSocket client, movement, camera, basic 3D world —
  only after the backend contract is stable.
- **E11 — Contracts & Versioning.** OpenAPI for REST, JSON Schema for WebSocket, versioning
  rules, command/event contracts, future UDP/ENet compatibility.
- **E12 — Observability & Load Testing.** Metrics, logs, traces, Kafka lag, soak tests.
- **E13 — Content Authoring Pipeline.** Seed scripts now → YAML/JSON import → admin panel.
- **E14 — Social World & Territory (the Lineage 2 layer).** The systems that make the shared
  world feel like L2: area-of-interest / interest management for a seamless world, clans &
  alliances, **sieges over places of power**, contested **World Echoes / world bosses** with
  fair shared spawn authority + anti-grief, player-driven **economy** (player shops, crafting,
  trade), persistent **reputation/identity**, and the chosen **PvP/conflict model**. Heavy
  systems — sequenced after the narrative core is proven, but the architecture from E06 onward
  must not preclude them (principle #4). Decide the PvP model with me before building it.

## 6. How I want you to work (working agreement)

- **One slice at a time.** Propose a short plan for the current phase, implement it, then
  show me what changed and how you verified it. Don't jump ahead phases.
- **Tests + CI green** before declaring a slice done. Add meaningful tests for engine logic
  (objective evaluation, unlock validation, reward granting), not just smoke tests.
- **Use real migrations** for schema (Flyway or Liquibase — pick one, justify briefly,
  stay consistent). Seed data is separate from schema.
- **Match the existing conventions** (package `ge.mmo.*`, module layout, env-var config
  style already in `application.yml` / `dev.env`). Reuse infra that exists; don't reinvent.
- **Windows-first dev.** I'm on Windows/PowerShell. Keep the `.bat` scripts working; any new
  dev command must run here.
- **Ask before irreversible or architecture-defining decisions** (DB schema shape for the
  engines, the command/event envelope format, build-tool/library choices). Recommend a
  default, don't just present a menu.
- **Commit discipline.** Small, descriptive commits; branch off `main` for multi-commit
  work; never push unless I ask. Keep the working tree honest — report failures with output.
- **Report faithfully.** If something is skipped, partial, or failing, say so plainly.

## 7. Start now

Begin with **Phase 0**. First give me a brief plan for the 4 hardening items (especially
the Spring Boot 4.x upgrade risk and the KRaft conversion), then execute item by item,
verifying each. After Phase 0 is green, stop and check in with me before starting E06.
