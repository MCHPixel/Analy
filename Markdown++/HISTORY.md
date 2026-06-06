
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

| Member                              | Description                                                                                                                                                                           |
|-------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `ConfigManager(Logger, Plugin)`     | Constructor — receives the plugin instance to access `getConfig()` and `getLogger()`                                                                                                  |
| `validate()`                        | Checks all fields for problems, logs a specific error per issue, returns `false` if anything is wrong, for this it uses Regular Expressions (regex) and validates structural patterns |
| `get_server_id()`                   | Returns `analy.server-id`                                                                                                                                                             |
| `get_api_key()`                     | Returns `analy.api-key`                                                                                                                                                               |
| `get_backend_url()`                 | Returns `analy.backend-url`                                                                                                                                                           |
| `get_flush_interval_seconds()`      | Returns `analy.buffer.flush-interval-seconds` (default: 10)                                                                                                                           |
| `get_max_buffer_size()`             | Returns `analy.buffer.max-size` (default: 500)                                                                                                                                        |
| `get_verify_certificate()`          | Returns `analy.tls.verify-certificate` (default: true)                                                                                                                                |

---