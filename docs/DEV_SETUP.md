# MMO Backend – Local Development Setup

## Purpose

This document describes how to set up and manage the local development environment
for the MMO backend project.

The goal is to allow any developer to clone the repository and have all required
infrastructure running locally within minutes, with minimal manual steps.

This setup is intended for:
- Backend development
- Local testing
- Debugging and experimentation

Production and staging environments are out of scope for this document.

---

## Prerequisites

Before starting, ensure the following tools are installed and available in your PATH:

- Windows 10 or newer
- Docker Desktop for Windows
- Git
- Java 17+ (JDK)

Recommended:
- IntelliJ IDEA (Ultimate or Community)
- Basic familiarity with Docker and command-line usage

Verify Docker is working by running:

```
docker --version
docker compose version
```

---

## Repository Structure (Relevant)

Only the parts relevant for local development are listed here.

```
mmo-backend/
├─ infrastructure/
│  └─ local/
│     ├─ docker-compose.yml
│     ├─ postgres/
│     │  └─ init.sql
│     └─ redis/
│        └─ data/
│
├─ scripts/
│  └─ windows/
│     ├─ dev-up.bat
│     ├─ dev-down.bat
│     ├─ dev-logs.bat
│     └─ dev-reset.bat
│
├─ docs/
│  └─ DEV_SETUP.md
│
└─ pom.xml
```

---

## Quick Start (TL;DR)

```
git clone <repository-url>
cd mmo-backend
scripts/windows/dev-up.bat
```

Stop infrastructure:
```
scripts/windows/dev-down.bat
```

View logs:
```
scripts/windows/dev-logs.bat
```

Full reset (destructive):
```
scripts/windows/dev-reset.bat
```

Kafka UI:
http://localhost:8081

---

## Detailed Setup

### 1. Start Local Infrastructure

```
scripts/windows/dev-up.bat
```

Starts PostgreSQL, Redis, Kafka, and supporting services using Docker Compose.

---

### 2. Stop Local Infrastructure

```
scripts/windows/dev-down.bat
```

Stops containers while preserving data.

---

### 3. View Logs

All services:
```
scripts/windows/dev-logs.bat
```

Single service:
```
scripts/windows/dev-logs.bat <service-name>
```

---

### 4. Reset Local Environment (Destructive)

```
scripts/windows/dev-reset.bat
```

Stops containers, removes volumes, and recreates everything from scratch.

---

## Service Endpoints

Service | Address
--------|---------
PostgreSQL | localhost:5432
Redis | localhost:6379
Kafka | localhost:9092
Kafka UI | http://localhost:8081

Database credentials (local):
- Database: mmorpg
- User: mmo
- Password: mmo

---

## Common Issues & Troubleshooting

- Kafka UI may show connection warnings during startup; this is expected locally.
- Ensure Docker Desktop is running before executing scripts.
- Ensure required ports are not already in use.

---

## Notes & Conventions

- Scripts are designed for Windows environments.
- Run scripts from the repository root.
- Do not modify infrastructure files unless you understand the impact.
