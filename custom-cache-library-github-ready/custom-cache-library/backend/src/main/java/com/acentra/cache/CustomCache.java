package com.acentra.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class CustomCache {
    private final int capacity;
    private final Map<String, CacheEntry> entries = new LinkedHashMap<>();
    private final AtomicLong hits = new AtomicLong();
    private final AtomicLong misses = new AtomicLong();
    private final AtomicLong evictions = new AtomicLong();

    private EvictionPolicy policy = new LruEvictionPolicy();

    public CustomCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be greater than zero");
        this.capacity = capacity;
    }

    public synchronized void put(String key, String value, long ttlSeconds) {
        cleanupExpired();
        entries.put(key, new CacheEntry(key, value, ttlSeconds));

        if (entries.size() > capacity) {
            String victim = policy.selectVictim(entries);
            if (victim != null && !victim.equals(key)) {
                entries.remove(victim);
                evictions.incrementAndGet();
            } else if (victim != null) {
                entries.remove(victim);
                evictions.incrementAndGet();
            }
        }
    }

    public synchronized String get(String key) {
        CacheEntry entry = entries.get(key);

        if (entry == null) {
            misses.incrementAndGet();
            return null;
        }

        if (entry.isExpired()) {
            entries.remove(key);
            misses.incrementAndGet();
            return null;
        }

        entry.accessed();
        hits.incrementAndGet();
        return entry.getValue();
    }

    public synchronized boolean remove(String key) {
        return entries.remove(key) != null;
    }

    public synchronized void clear() {
        entries.clear();
    }

    public synchronized Map<String, CacheEntry> snapshot() {
        cleanupExpired();
        return new LinkedHashMap<>(entries);
    }

    public synchronized void setPolicy(String policyName) {
        if ("LRU".equalsIgnoreCase(policyName)) {
            policy = new LruEvictionPolicy();
        } else if ("LFU".equalsIgnoreCase(policyName)) {
            policy = new LfuEvictionPolicy();
        } else {
            throw new IllegalArgumentException("Policy must be LRU or LFU");
        }
    }

    public synchronized String getPolicy() {
        return policy.name();
    }

    public synchronized CacheMetrics metrics() {
        cleanupExpired();
        long h = hits.get();
        long m = misses.get();
        long total = h + m;
        double hitRate = total == 0 ? 0 : (h * 100.0) / total;
        double missRate = total == 0 ? 0 : (m * 100.0) / total;
        return new CacheMetrics(h, m, evictions.get(), entries.size(), total, hitRate, missRate);
    }

    public int capacity() {
        return capacity;
    }

    private void cleanupExpired() {
        entries.entrySet().removeIf(e -> e.getValue().isExpired());
    }
}
