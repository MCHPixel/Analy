package com.mchpixel.analy.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Tags {

    // Use this when you have nothing extra to attach to a metric
    public static final Map<String, String> EMPTY = Collections.emptyMap();

    // Tags.of("key1", "value1", "key2", "value2")
    public static Map<String, String> of(String... pairs) {
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("Tags.of() needs pairs — key then value, key then value");
        }
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put(pairs[i], pairs[i + 1]);
        }
        return Collections.unmodifiableMap(map);
    }
}