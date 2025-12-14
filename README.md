# mmo-backend

Java/Spring Boot MMO backend monorepo (multi-module Maven).  
Current focus: **local development environment (E05)** + foundation services.

---

## What’s in this repo (today)

### Local infrastructure (Docker Compose)
Runs locally via Docker Compose:

- **PostgreSQL** (db: `mmorpg`, user/pass: `mmo`)
- **Redis**
- **Kafka + Zookeeper**
- **Kafka UI**
- **Observability stack**
    - Prometheus
    - Grafana
    - Loki + Promtail

### First service
- `services/auth-service` (Spring Boot + Actuator)

---

## Quick start (Windows)

> All scripts assume you run them from repo root.

### Start local infrastructure
```bat
scripts\windows\dev-up.bat dev
View logs

scripts\windows\dev-logs.bat
Or a single service:


scripts\windows\dev-logs.bat postgres
Reset everything (DESTRUCTIVE)
Stops containers, deletes volumes, recreates infra, and runs seed SQL scripts:


scripts\windows\dev-reset.bat dev
Stop local infrastructure (keeps volumes)

scripts\windows\dev-down.bat dev
Endpoints & Ports
Kafka UI: http://localhost:8081

Grafana: http://localhost:3000

Prometheus: http://localhost:9090

Loki readiness: http://localhost:3100/ready

PostgreSQL:

Host: localhost

Port: 5432

Database: mmorpg

User: mmo

Password: mmo

Redis:

Host: localhost

Port: 6379

Seed data (local dev)
Init SQL: infrastructure/local/postgres/init.sql

Seed scripts directory: infrastructure/local/postgres/seed/*.sql

dev-reset.bat automatically applies all *.sql files in the seed folder.

To verify seed runs:


docker exec -it mmo_postgres psql -U mmo -d mmorpg -c "select * from mmo_meta.seed_runs order by executed_at desc;"
Configuration
Local compose uses env files:

infrastructure/local/env/dev.env

(Other env files like staging.env / prodlike.env may exist but are intentionally ignored by git.)

Documentation
Development setup guide: docs/DEV_SETUP.md

Contributing / Workflow
Branch: main

Commit small, focused changes.

Keep local data directories out of git (Postgres/Redis/Grafana/Loki bind mounts are ignored).