package com.acentra.cache;

import java.util.Map;

public class LruEvictionPolicy implements EvictionPolicy {
    @Override
    public String name() {
        return "LRU";
    }

    @Override
    public String selectVictim(Map<String, CacheEntry> entries) {
        return entries.values().stream()
                .min((a, b) -> Long.compare(a.getLastAccessedAt(), b.getLastAccessedAt()))
                .map(CacheEntry::getKey)
                .orElse(null);
    }
}
