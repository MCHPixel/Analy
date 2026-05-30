package com.mchpixel.analy.model;

public enum MetricType {
    COUNTER("counter"),
    GAUGE("gauge"),
    DURATION("duration"),
    EVENT("event"),
    RATIO("ratio"),
    PRICE("price");

    public final String jsonName;

    MetricType(String jsonName) {
        this.jsonName = jsonName;
    }
}