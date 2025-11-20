
package com.example.Music_Player.Model;

import java.util.LinkedHashMap;
import java.util.Map;
@Deprecated
public class CacheMap<K, V> extends LinkedHashMap<K, V> {
    int capacity;

    public CacheMap(int capacity) {
        super(capacity, 0.75F, true);
        this.capacity = capacity;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > this.capacity;
    }
}
