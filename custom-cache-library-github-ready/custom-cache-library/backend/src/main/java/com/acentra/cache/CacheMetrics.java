package com.acentra.cache;

public record CacheMetrics(
        long hits,
        long misses,
        long evictions,
        int size,
        long totalRequests,
        double hitRate,
        double missRate
) {}
