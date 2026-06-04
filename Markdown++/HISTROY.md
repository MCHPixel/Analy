### What we did
Built the `ConfigManager` — reads all values from `config.yml` on startup and validates them before anything else runs. If anything is wrong the plugin shuts down cleanly with a clear error instead of crashing later.

### Commit
```
feat: add config manager and validation
```
```
- Add ConfigManager to load config.yml values into typed fields
- Add validation for required fields, placeholder detection, URL format and value ranges
- Wire ConfigManager into Analy main class with early exit on invalid config
```

#### `ConfigManager.java`
*`com.mchpixel.analy.core`*

Wraps PaperMC's built-in `getConfig()` and exposes all config values as typed fields. Also validates the entire config on startup and reports every problem clearly before anything else initialises.

| Member | Description |
|--------|-------------|
| `ConfigManager(JavaPlugin)` | Constructor — receives the plugin instance to access `getConfig()` and `getLogger()` |
| `load()` | Reads all values from `config.yml` into fields, called by the constructor |
| `validate()` | Checks all fields for problems, logs a specific error per issue, returns `false` if anything is wrong |
| `getServerId()` | Returns `analy.server-id` |
| `getApiKey()` | Returns `analy.api-key` |
| `getBackendUrl()` | Returns `analy.backend-url` |
| `getFlushIntervalSeconds()` | Returns `analy.buffer.flush-interval-seconds` (default: 10) |
| `getMaxBufferSize()` | Returns `analy.buffer.max-size` (default: 500) |
| `isTlsVerify()` | Returns `analy.tls.verify-certificate` (default: true) |

---