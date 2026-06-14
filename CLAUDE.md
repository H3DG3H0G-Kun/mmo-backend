# CLAUDE.md

Guidance for Claude Code working in this repository. Auto-loaded each session.

## What this project is

Backend-first **Georgian historical-fantasy MMO** ("Watcher"). The player is a soul that
**remembers, witnesses, and restores** Georgian memory across eras — History, Myth, and Word
(literature), all modeled by one system: the `Saga → Thread → Beat` narrative graph, opened by
**Resonance Points** in the world. Two canonical docs:
- **[docs/GAME_DESIGN.md](docs/GAME_DESIGN.md)** — the product & story system (the *what/why*; the soul).
- **[docs/BUILD_PROMPT.md](docs/BUILD_PROMPT.md)** — architecture, engines, phased roadmap (the *how*).

Read both before feature work.

## Architecture principles (do not violate)

1. **Systems are code. Stories are data.** No story/tale/boss/era/trigger hard-coded in Java.
   Build generic engines (narrative registry, trigger/resonance, instance, interaction
   evaluator, encounter, outcome, reward/unlock, world/timeline, party, codex); author all
   content as data (seed SQL now, JSON/YAML + admin later). A whole new poem/chronicle = data
   rows, **zero engine changes**.
2. **Backend is the source of truth.** Client sends intentions; backend validates and decides.
   No important state is client-authoritative.
3. **Contract over transport.** REST/HTTP/2 for account-meta, WebSocket/JSON for realtime.
   Commands/events stay transport-independent (future UDP/ENet/protobuf without rewrites).
4. **World feeling = Lineage 2, not WoW/Aion.** Seamless persistent *shared* world is the
   default home; instanced Threads are the exception. Design for interdependence, risk,
   territory/politics (clans/sieges), a player-driven social economy, and persistent identity.
   Architecture must allow area-of-interest/interest management + durable world/social state
   from the start. No WoW-style phasing or teleport-everywhere convenience. (GAME_DESIGN §7.)

## Stack & layout

- Java 25, Spring Boot, multi-module Maven monorepo, group `ge.mmo`.
- `services/<name>-service/` — Spring Boot services (only `auth-service` exists today).
- `infrastructure/local/` — Docker Compose (Postgres 16, Redis 7, Kafka, Kafka UI,
  Prometheus, Loki, Promtail, Grafana) + Postgres init/seed.
- `scripts/windows/` — `dev-up.bat`, `dev-down.bat`, `dev-logs.bat`, `dev-reset.bat dev`.
- `contracts/` — REST/WebSocket schemas + protocol versions (planned).
- `clients/godot-client/` — Godot 4.5.1 client (planned, comes after backend contract is stable).

## Working agreement

- **Windows / PowerShell** is the dev environment. Keep `.bat` scripts working; new dev
  commands must run here.
- Build in **vertical slices**, one phase at a time (see roadmap). Propose plan → implement →
  show changes + how verified. Don't jump ahead phases.
- **Tests + CI green** (`mvn -B -ntp verify`) before declaring a slice done.
- Use real DB **migrations** for schema; keep seed data separate.
- Match existing conventions (`ge.mmo.*` packages, env-var config in `application.yml` / `dev.env`).
- **Ask before** architecture-defining or irreversible decisions (engine schema, command/event
  envelope, build-tool/library choices) — recommend a default, don't just list options.
- Small descriptive commits; branch off `main` for multi-commit work; **never push unless asked**.
- Report faithfully — if something is skipped/partial/failing, say so with the output.

## Current state (keep this section honest as work lands)

- **Phase 0 (hardening) DONE & verified:** CI fixed (uses Maven wrapper), Kafka on **KRaft**
  (no ZooKeeper, boot-verified), **Spring Boot 4.0.0** (`mvnw verify` green on Java 25),
  `contracts/` + `clients/godot-client/` created, Maven wrapper added.
- Foundation done: Docker Compose infra, observability baseline, Windows dev scripts,
  seed reset (`dev-reset.bat dev`, verified via `mmo_meta.seed_runs`).
- **E06 DONE.** `auth-service` (register/login/JWT + `/me`), `libs/common` shared JwtService,
  `world-service` (character creation, world/era entry, WebSocket handshake). Verified
  end-to-end live across both services (REST + `/ws`).
- **E07 DONE.** Narrative engine in `world-service` (`narrative` package): `Saga → Tale → Beat`
  graph, composable Resonance `TriggerCondition`s, `ResonanceEvaluator`, `NarrativeService`
  (resonances/enter/advance with `TaleProgress`), REST under `/api/narrative`, Flyway `V2`
  seeding the Parnavaz tale. Verified live: walk→resonate→enter→Witness/Ward→complete→era unlock.
  (A "Tale" in code = the design's "Thread".)
- **E09 DONE.** Timeline in `world-service` (`timeline` package): per-character `character_era_unlock`,
  server-wide `timeline_progress` (Living Timeline), `TimelineService` (unlock/isUnlocked/listEras/
  travel/recordRestoration), REST under `/api/world` (eras, travel, timeline), Flyway `V3`. Tale
  completion now persistently unlocks the era + increments the Living Timeline. Verified live:
  locked era → 409, complete tale → era unlocks → travel 200 → counter ticks.
- **E08 DONE.** Party (`party` pkg, Flyway `V4`): create/join/leave(promote)/disband, one party
  per character. **Co-op Tale instances** (`tale_instance`, Flyway `V5`): `InstanceService` —
  leader starts a party-shared run, any member advances the shared beat, completion fans the
  reward out to every member (one Living-Timeline restoration). Shared beat-graph traversal
  extracted to `BeatNavigator`; resonance-context building to `ResonanceContextFactory` (used by
  solo + co-op). REST under `/api/party` and `/api/narrative/party/*`. Verified live: leader-only
  start (403), one-active (409), Bob→Alice→Bob advance, both members get era 2.
- **E11 (contracts v1) DONE.** Hand-authored contract for the live backend in `contracts/`:
  `rest/endpoints.md`, `ws/protocol.md`, `VERSIONS.md`. Source of truth for clients (Godot);
  machine-readable OpenAPI/JSON-Schema generation can layer on later.
- **E14 (clans) DONE.** `clan` pkg (Flyway `V6`): `clan` + `clan_member` + `clan_invite`,
  ranks LEADER/OFFICER/MEMBER, `ClanService` (create/invite/accept/leave[+promote]/kick/setRank
  [leadership transfer]/myClan), backend rank guards, REST under `/api/clans`. Verified live:
  create→dup-tag 409→invite→accept→rank guards (member/officer can't outrank leader)→promote.
- **Still open:** public **World Echoes** (server-shared open-world tale events); **sieges**
  over places of power; the **player economy** (the rest of E14).
- **Next:** sieges (clans contest places of power) → economy, then E10 Godot.

## Build / verify commands (Windows)

- Build + test: `./mvnw.cmd -B -ntp verify` (or `mvnw.cmd` from repo root).
- Local infra: `scripts\windows\dev-up.bat` / `dev-down.bat` / `dev-reset.bat dev`.
- Note: repo lives under OneDrive — if `clean` fails on a locked `target/`, delete it manually
  (OneDrive sync lock), then re-run.
- **Gotcha:** this machine has a **native Postgres** service also listening on host `5432`,
  which shadows the Docker `mmo_postgres` container for `localhost` connections (and has no
  `mmo` role). When running a service against the container, either stop the native Postgres
  service or publish the container on another port (e.g. `POSTGRES_PORT=55432 docker compose
  ... up -d --force-recreate postgres` and set `MMO_DB_PORT=55432`).
