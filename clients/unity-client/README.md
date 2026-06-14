# Unity client (scaffold)

A **drop-in** Unity client for the Watcher backend — the "prove the pipe" milestone:
log in → open a WebSocket → spawn into the world → walk with WASD → **see other Watchers move
in real time**. UI for tales/clans/combat layers on later.

> Scaffold status: this is real, idiomatic Unity C# written against the stable
> [contracts](../../contracts), but it was **not compiled in a Unity editor here**. Treat it as
> a strong starting point you open and iterate on.

## Requirements
- **Unity 6 LTS** (6000.x) — a **3D** project (Built-in or URP, either is fine).
- One package: **Newtonsoft JSON** (`com.unity.nuget.newtonsoft-json`). Add via
  *Window → Package Manager → + → Add package by name…* → `com.unity.nuget.newtonsoft-json`.
  (WebSockets use the built-in `System.Net.WebSockets`, so no other package is needed on
  desktop/standalone. For a WebGL build later, swap `GameSocket` to the `NativeWebSocket` package.)

## Setup
1. Create a new Unity 6 **3D** project (e.g. `WatcherClient`).
2. Add the Newtonsoft package (above).
3. Copy this folder's **`Assets/Scripts`** into your project's `Assets/`.
4. In an empty scene:
   - Add a ground plane (GameObject → 3D Object → Plane).
   - Create an empty GameObject named `Game`, add the **`GameBootstrap`** component.
   - Fill the inspector fields: Auth URL `http://localhost:8080`, WS URL `ws://localhost:8090/ws`,
     World URL `http://localhost:8090`, a username/password, and a character name.
     (When running services locally on the alt ports, use `:18080` / `:18090`.)
5. **Input:** movement uses the classic Input Manager (`Input.GetAxisRaw`). Set
   *Project Settings → Player → Active Input Handling* to **"Both"** (or "Input Manager (Old)").
6. Start the backend stack (`scripts\windows\dev-up.bat` + run the services), then press **Play**.
   A capsule is your Watcher; other connected clients appear as capsules and move in real time.
   WASD / arrow keys move you; movement is sent to the server and broadcast to others in your era.

## What's here
```
Assets/Scripts/
  Net/Protocol.cs      // command/event type + error code constants (mirror of the WS contract)
  Net/Dtos.cs          // serializable DTOs (REST responses + WS payloads)
  Net/ApiClient.cs     // REST: login, list/create character (UnityWebRequest, async)
  Net/GameSocket.cs    // WebSocket: connect, hello, enter-world, move; raises typed events
  World/RemoteWatcher.cs // a remote Watcher capsule (smooth position lerp)
  World/WorldManager.cs  // applies snapshot/join/move/left; spawns local + remote watchers
  GameBootstrap.cs     // orchestrates login -> ensure character -> connect -> enter world
```

## How it maps to the backend
- REST (`ApiClient`): `POST /api/auth/login`, `GET/POST /api/characters` — see
  [contracts/rest/endpoints.md](../../contracts/rest/endpoints.md).
- WebSocket (`GameSocket`): `SESSION_HELLO → SESSION_WELCOME`, `ENTER_WORLD → WORLD_ENTERED` +
  `WORLD_SNAPSHOT`, `MOVE → ENTITY_MOVED`, plus `ENTITY_JOINED` / `ENTITY_LEFT` — see
  [contracts/ws/protocol.md](../../contracts/ws/protocol.md). The backend is authoritative; this
  client only sends intentions and renders what the server reports.

## Next milestones (after the pipe works)
Tale screens (narrative REST), party/clan/siege panels, combat UI, real art/animation, a proper
login scene, then a WebGL/mobile build (switch to NativeWebSocket).
