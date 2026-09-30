package com.acentra.cache;

public final class CacheEntry {
    private final String key;
    private final String value;
    private final long createdAt;
    private final long expiresAt;
    private long frequency;
    private long lastAccessedAt;

    public CacheEntry(String key, String value, long ttlSeconds) {
        this.key = key;
        this.value = value;
        this.createdAt = System.currentTimeMillis();
        this.expiresAt = ttlSeconds > 0
                ? createdAt + ttlSeconds * 1000L
                : Long.MAX_VALUE;
        this.frequency = 0;
        this.lastAccessedAt = createdAt;
    }

    public String getKey() { return key; }
    public String getValue() { return value; }
    public long getCreatedAt() { return createdAt; }
    public long getExpiresAt() { return expiresAt; }
    public long getFrequency() { return frequency; }
    public long getLastAccessedAt() { return lastAccessedAt; }

    public void accessed() {
        frequency++;
        lastAccessedAt = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expiresAt;
    }
}
