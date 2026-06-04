## What we've done ✅

**Project Direction**
- Rewrote the entire project spec — shifted from a Node.js/TypeScript stack to a self-hosted Docker Compose setup
- Java plugin, Go backend, server-side rendered dashboard (HTMX + SSE), no JavaScript frameworks
- Wrote a full updated `Project_Analy_v2.md` covering every component in detail

**Plugin — Model Layer**
- `MetricType.java` — enum with `jsonName` field for clean JSON serialization
- `Tags.java` — helper class with `Tags.of("key", "value")` pair syntax
- `Metric.java` — core data container with full and shortcut constructors

**Plugin — Buffer**
- `MetricBuffer.java` — thread-safe `ArrayBlockingQueue` with `add()`, `flush()`, and `isEmpty()` methods

**Plugin — Collectors**
- `PlayerEventCollector.java` — listens for `PlayerJoinEvent` and `PlayerQuitEvent`, creates metrics and drops them in the buffer

**Plugin — Infrastructure**
- `DebugCommand.java` — `/debuganaly` command that toggles debug mode on/off, logs metrics to console when enabled
- `Analy.java` — main plugin entry point, wires everything together, handles graceful shutdown flush

**Docs**
- Wrote an extensive `README.md` covering installation, configuration, commands, metrics reference, developer API, project structure, and full roadmap

---

## What still needs to be done 🔧

**Plugin — Phase 2**
- Finish remaining collectors — TPS + RAM sampling, player death, session duration tracking
- HMAC-SHA256 request signing on outbound metric batches
- HTTP client (`ApiClient.java`) to actually POST metrics to the Go backend instead of just logging them
- `config.yml` loading via `ConfigManager.java`
- `plugin.yml` with proper command permissions

**Plugin — Phase 3**
- Public `analy-api` jar for third party plugin developers
- `Analytics.java` static facade API

**Backend — Go**
- Entire Go backend is not started yet:
    - `POST /api/v1/ingest` endpoint
    - API key generation + Argon2id storage
    - HMAC signature verification middleware
    - Replay protection (nonce + timestamp window)
    - Rate limiting (Redis sliding window)
    - TimescaleDB schema + migrations
    - Dashboard login + cookie session (JWT)
    - SSR dashboard pages with `html/template`
    - SSE hub + Redis pub/sub for live updates
    - Metric query endpoints
    - Player and server management endpoints

**Infrastructure**
- `docker-compose.yml`
- `Caddyfile`
- Go `Dockerfile` (multi-stage)
- `.env.example`

**Dashboard Pages**
- Overview, Players, Server Health, Economy, Metric Explorer

**Phases 4–6**
- Alerting system, Discord webhooks
- Economy tracking (Vault integration)
- Multi-server support
- Custom dashboard builder
- Admin panel

---