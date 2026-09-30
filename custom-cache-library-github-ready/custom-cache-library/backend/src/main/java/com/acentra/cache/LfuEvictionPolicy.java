package com.acentra.cache;

import java.util.Map;

public class LfuEvictionPolicy implements EvictionPolicy {
    @Override
    public String name() {
        return "LFU";
    }

    @Override
    public String selectVictim(Map<String, CacheEntry> entries) {
        return entries.values().stream()
                .min((a, b) -> {
                    int frequencyCompare = Long.compare(a.getFrequency(), b.getFrequency());
                    if (frequencyCompare != 0) return frequencyCompare;
                    return Long.compare(a.getLastAccessedAt(), b.getLastAccessedAt());
                })
                .map(CacheEntry::getKey)
                .orElse(null);
    }
}
