I want to maintain a History.md so you give me this additionally:

    Short Description of the Change with a Commit message and a Small commit Description
    Short Description of what we did
    Every class / function with what it is supposed to do


Here is an Example

(what-we-did)
### What we did

We built the foundation of the plugin
(what-we-did)


### Commit
```
feat: initialized something
```
```
- Add Added xyz with xyz thing
- Fix Fixed xyz with xyz class thing
...
```


(class-name)
#### `MetricType.java`
(/class-name)
(class-location)
*`com.mchpixel.analy.model`*
(/class-location)


(short-description)
An enum representing the category of a metric. Each value carries a `jsonName` string used when serializing to JSON for the backend.
(/short-description)

(function-with-use)

 Member | Description |
|--------|-------------|
| `Metric(String, double, MetricType, long, Map)` | Full constructor — specify everything including timestamp and tags |
| `Metric(String, double, MetricType)` | Shortcut constructor — timestamp set to now, empty tags |
| `getMetric()` | Returns the dot-notation metric name e.g. `"player.action.join"` |
| `getValue()` | Returns the numeric value |
| `getType()` | Returns the `MetricType` |
| `getTimestamp()` | Returns Unix timestamp in milliseconds |
| `getTags()` | Returns the immutable tag map |
| `toString()` | Human-readable representation for logging |

(/function-with-use)

---