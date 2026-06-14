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
- **Next: E06 — MMO Foundation Slice** (auth → character → world entry → narrative core).

## Build / verify commands (Windows)

- Build + test: `./mvnw.cmd -B -ntp verify` (or `mvnw.cmd` from repo root).
- Local infra: `scripts\windows\dev-up.bat` / `dev-down.bat` / `dev-reset.bat dev`.
- Note: repo lives under OneDrive — if `clean` fails on a locked `target/`, delete it manually
  (OneDrive sync lock), then re-run.
