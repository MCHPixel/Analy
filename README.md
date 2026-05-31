# Analy

> A lightweight, self-hostable telemetry and analytics platform for Minecraft servers.

Analy is a PaperMC plugin that silently collects player and server events in the background and ships them to a self-hosted analytics backend for real-time dashboards and historical analysis. No cloud accounts. No external services. Just a Docker Compose stack and a jar in your plugins folder.

---

## Table of Contents

- [What is Analy?](#what-is-analy)
- [How it Works](#how-it-works)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration](#configuration)
- [Commands](#commands)
- [Metrics Collected](#metrics-collected)
- [Plugin Developer API](#plugin-developer-api)
- [Project Structure](#project-structure)
- [Development](#development)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)

---

## What is Analy?

Analy is an **observability platform** for Minecraft server operators — the same category of tool as Prometheus or Grafana, but purpose-built for Minecraft and trivially easy to self-host.

It answers questions like:

- How many players joined today vs last week?
- What time of day is my server busiest?
- How long do players stay on average?
- When did my TPS drop and what was happening at the time?
- What items are being traded most in my economy?

Instead of hardcoding specific stats, Analy is built around a **generic metric pipeline** — every data point flows through the same structure regardless of what it represents. This means any event, from a player death to a custom boss kill, is tracked and queryable the same way.

---

## How it Works

```
Minecraft Event (e.g. player joins)
  → Plugin Listener fires
    → Metric created (name, value, type, timestamp, tags)
      → Dropped into in-memory buffer
        → Every 10 seconds: buffer flushes to Go backend
          → Backend validates + stores in TimescaleDB
            → Dashboard updates in real time
```

The plugin never talks to the database directly. It only knows about the backend URL and its API key. The backend handles everything else.

### Why a buffer?

Instead of sending one HTTP request per event (which would hammer the backend on a busy server), the plugin collects metrics in memory and sends them in batches. This means a server with 100 players all dying at once sends **one** HTTP request with 100 metrics, not 100 requests.

---

## Features

### Plugin
- Lightweight — minimal performance impact on the server
- Thread-safe in-memory metric buffer (`ArrayBlockingQueue`)
- Configurable flush interval and buffer size
- Graceful shutdown — flushes remaining metrics before the server stops
- HMAC-SHA256 signed requests — metrics can't be spoofed
- Debug mode toggle via command — see every metric in real time
- Extensible collector system — easy to add new event types
- Public Java API for third-party plugin integration

### Backend (Go)
- Single binary — serves both the REST API and the web dashboard
- Server-side rendered dashboard — no JavaScript framework, no Node.js
- Live updates via HTMX + Server-Sent Events
- TimescaleDB for efficient time-series storage and aggregation
- Redis for rate limiting, pub/sub, and replay protection
- Automatic HTTPS via Caddy
- Ships as a tiny ~20MB Docker image

### Dashboard
- Real-time stat cards (TPS, player count, RAM)
- Player join/leave history
- Session duration analytics
- Economy tracking
- Custom metric explorer — any metric registered by any plugin appears automatically
- No JavaScript framework — just HTML, HTMX, and a tiny SVG chart helper

---

## Requirements

### Plugin
- Java 17 or higher
- PaperMC 1.20 or higher (Spigot compatible for most features)
- A running Analy backend (see backend setup)

### Backend (Self-Hosted)
- Docker and Docker Compose
- A domain or local network address
- 512MB RAM minimum (2GB recommended)

---

## Installation

### 1. Set up the backend

Clone the repository and start the Docker stack:

```bash
git clone https://github.com/yourusername/analy.git
cd analy
cp .env.example .env
# Edit .env and fill in your passwords and keys
docker compose up -d
```

The dashboard will be available at `https://your-domain.com/dashboard`.

### 2. Register your server

Log into the dashboard, go to **Servers → Add Server**, give it a name, and copy the generated **Server ID** and **API Key**.

### 3. Install the plugin

Download the latest `analy-plugin.jar` from the [releases page](https://github.com/yourusername/analy/releases) and drop it into your server's `plugins/` folder.

### 4. Configure the plugin

Edit `plugins/Analy/config.yml`:

```yaml
analy:
  server-id: "your-server-id"
  api-key: "your-api-key"
  backend-url: "https://your-domain.com"
```

### 5. Restart your server

You should see in console:

```
[Analy] Analy started! :3
```

That's it. Metrics are now flowing.

---

## Configuration

Full `config.yml` reference:

```yaml
analy:
  # Your server's unique ID from the dashboard
  server-id: "srv_01hx4k2..."

  # Your API key from the dashboard — keep this secret!
  api-key: "your-api-key"

  # URL of your Analy backend
  backend-url: "https://analytics.yourdomain.com"

  buffer:
    # How often to flush metrics to the backend (in seconds)
    flush-interval-seconds: 10

    # Maximum metrics to hold before forcing a flush
    max-size: 500

  collectors:
    # Toggle individual collectors on or off
    player-events: true
    server-health: true
    economy: false       # Requires Vault

  tls:
    # Set to false only for local development with self-signed certs
    verify-certificate: true
```

---

## Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/debuganaly` | `analy.debug` | Toggle debug mode on/off. When enabled, every metric is printed to console the moment it is created. |

### Debug Mode

Debug mode is useful when developing a new collector or third-party integration. When enabled, every metric that passes through the buffer gets printed to console immediately:

```
[Analy Debug] metric added: Metric{metric='player.action.join', value=1.0, type=COUNTER, timestamp=1748510200000, tags={player_uuid=550e8400-...}}
```

Toggle it off when you're done — it's noisy on a busy server.

---

## Metrics Collected

Analy collects the following metrics out of the box. All metrics follow the `domain.subject.measurement` naming convention.

### Player Events

| Metric | Type | Tags | Description |
|--------|------|------|-------------|
| `player.action.join` | COUNTER | `player_uuid` | Player joined the server |
| `player.action.quit` | COUNTER | `player_uuid` | Player left the server |
| `player.death.count` | COUNTER | `player_uuid`, `world` | Player died |
| `player.session.duration` | DURATION | `player_uuid` | Length of a play session in ms |

### Server Health

| Metric | Type | Tags | Description |
|--------|------|------|-------------|
| `server.tps.current` | GAUGE | — | Current ticks per second (0–20) |
| `server.memory.used_mb` | GAUGE | — | Heap memory currently in use |
| `server.memory.max_mb` | GAUGE | — | Maximum configured heap |
| `server.players.online` | GAUGE | — | Currently connected player count |

### Economy (optional, requires Vault)

| Metric | Type | Tags | Description |
|--------|------|------|-------------|
| `economy.balance.change` | GAUGE | `player_uuid` | Change in a player's balance |
| `economy.item.price` | PRICE | `item`, `market` | Current price of an item |
| `economy.transaction.volume` | COUNTER | — | Total value transacted |

---

## Plugin Developer API

Third-party plugins can emit their own custom metrics into the Analy pipeline with a few lines of Java. Custom metrics appear in the dashboard automatically under the `custom.*` namespace.

### Dependency

**Maven:**
```xml
<dependency>
  <groupId>dev.analy</groupId>
  <artifactId>analy-api</artifactId>
  <version>1.0.0</version>
  <scope>provided</scope>
</dependency>
```

**Gradle:**
```groovy
compileOnly 'dev.analy:analy-api:1.0.0'
```

### Usage

```java
import dev.analy.api.Analytics;
import dev.analy.api.Tags;
import dev.analy.api.MetricType;

// Count something
Analytics.increment("custom.crafting.table_opens");

// Track a current value
Analytics.gauge("custom.villager.count", villagerCount);

// Track how long something took
Analytics.duration("custom.boss.fight_duration_ms", fightTimeMillis);

// Record that something happened, with context
Analytics.event("custom.boss.killed",
    Tags.of("boss", "ender_dragon", "difficulty", "hard"));

// Full control
Analytics.track(
    "custom.shop.item_sold",
    salePrice,
    MetricType.PRICE,
    Tags.of("item", item.getType().name(), "shop", shopName)
);
```

### Register a metric definition (optional)

Registering a definition lets the dashboard display your metric with a human-readable name and description:

```java
Analytics.registerMetric(new MetricDefinition.Builder()
    .name("custom.boss.fight_duration_ms")
    .displayName("Boss Fight Duration")
    .description("Time from boss spawn to kill in milliseconds")
    .unit("ms")
    .type(MetricType.DURATION)
    .suggestedChart(ChartType.LINE)
    .build()
);
```

### Lifecycle

Always check Analy is present before using it:

```java
@Override
public void onEnable() {
    if (getServer().getPluginManager().getPlugin("Analy") == null) {
        getLogger().warning("Analy not found — custom metrics disabled");
        return;
    }
    Analytics.registerMetric(/* ... */);
}
```

---

## Project Structure

```
analy/
├── plugin/                          # Java PaperMC plugin
│   └── src/main/java/com/mchpixel/analy/
│       ├── Analy.java               # Plugin entry point
│       ├── collectors/
│       │   └── PlayerEventCollector.java
│       ├── model/
│       │   ├── Metric.java
│       │   ├── MetricType.java
│       │   ├── MetricBuffer.java
│       │   └── Tags.java
│       └── tests/
│           └── DebugCommand.java
├── backend/                         # Go backend + dashboard
│   ├── cmd/analy/main.go
│   ├── internal/
│   │   ├── api/                     # REST API handlers
│   │   ├── web/                     # SSR dashboard handlers
│   │   ├── middleware/              # Auth, rate limiting, replay protection
│   │   ├── service/                 # Business logic
│   │   └── db/                      # Database queries + migrations
│   ├── templates/                   # Go html/template files
│   └── static/                      # CSS + htmx.min.js
├── docker-compose.yml
├── Caddyfile
├── .env.example
└── README.md
```

---

## Development

### Running locally

```bash
# Start the backend stack (postgres, redis, caddy)
docker compose -f docker-compose.dev.yml up -d

# Run the Go backend
cd backend
go run ./cmd/analy

# Build the plugin
cd plugin
mvn package
# jar is in target/analy-plugin.jar
```

### Running a test server

1. Download a PaperMC jar from [papermc.io](https://papermc.io)
2. Create a test server folder and drop the jar in
3. Build the plugin and copy `target/analy-plugin.jar` to `plugins/`
4. Fill in `plugins/Analy/config.yml`
5. Start the server and watch the console

### Debug mode

During development use `/debuganaly` to toggle debug output — every metric will print to console the moment it is created so you can verify your collectors are working without waiting for the flush interval.

---

## Roadmap

### Phase 1 — Foundation ✅
- [x] Metric model (`Metric`, `MetricType`, `Tags`)
- [x] Thread-safe metric buffer
- [x] Player join/quit collectors
- [x] Debug command
- [x] Plugin entry point with graceful shutdown

### Phase 2 — Authentication & Core Collectors
- [ ] HMAC-SHA256 request signing
- [ ] API key generation and validation
- [ ] Replay protection (nonce + timestamp)
- [ ] Rate limiting
- [ ] Session duration tracking
- [ ] TPS + RAM sampling
- [ ] Player death collector
- [ ] Dashboard login + session

### Phase 3 — Generic Metric System
- [ ] Metric definitions table
- [ ] Tag-based filtering
- [ ] Metric discovery endpoint
- [ ] Public Java API jar (`analy-api`)
- [ ] TimescaleDB continuous aggregates

### Phase 4 — Real-Time & Alerting
- [ ] Redis pub/sub → SSE hub
- [ ] Live dashboard updates via HTMX
- [ ] Alert rules (metric + condition + threshold)
- [ ] Discord webhook notifications

### Phase 5 — Economy & Advanced Analytics
- [ ] Vault API integration
- [ ] Economy dashboard
- [ ] Player retention cohort table
- [ ] Leaderboards

### Phase 6 — Platform
- [ ] Multi-server support
- [ ] Custom dashboard builder
- [ ] Public shareable dashboards
- [ ] Admin panel

---

## Contributing

Contributions are welcome! If you want to add a new collector, fix a bug, or improve the dashboard, feel free to open a pull request.

Please follow the existing code style:
- Java: standard PaperMC plugin conventions
- Go: `gofmt` formatted, idiomatic Go
- Commit messages: `feat:`, `fix:`, `docs:` prefixes

---

## License

MIT License — do whatever you want with it, just don't remove the license header.
