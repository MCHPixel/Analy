package com.mchpixel.analy.model;

import java.util.Map;

public class Metric {

    private final String metric;
    private final double value;
    private final MetricType type;
    private final long timestamp;
    private final Map<String, String> tags;

    // Full constructor — you control everything
    public Metric(String metric, double value, MetricType type, long timestamp, Map<String, String> tags) {
        this.metric = metric;
        this.value = value;
        this.type = type;
        this.timestamp = timestamp;
        this.tags = tags;
    }

    // Shortcut constructor — timestamp is set to right now, no tags
    // Good for simple metrics like server TPS where you don't need extra context
    public Metric(String metric, double value, MetricType type) {
        this(metric, value, type, System.currentTimeMillis(), Tags.EMPTY);
    }

    public String getMetric()            { return metric; }
    public double getValue()             { return value; }
    public MetricType getType()          { return type; }
    public long getTimestamp()           { return timestamp; }
    public Map<String, String> getTags() { return tags; }

    @Override
    public String toString() {
        return "Metric{" +
                "metric='" + metric + '\'' +
                ", value=" + value +
                ", type=" + type +
                ", timestamp=" + timestamp +
                ", tags=" + tags +
                '}';
    }
}