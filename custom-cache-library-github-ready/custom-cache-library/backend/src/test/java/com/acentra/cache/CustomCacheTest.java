package com.acentra.cache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomCacheTest {

    @Test
    void shouldPutAndGet() {
        CustomCache cache = new CustomCache(3);
        cache.put("A", "Alpha", 0);
        assertEquals("Alpha", cache.get("A"));
    }

    @Test
    void shouldEvictUsingLru() {
        CustomCache cache = new CustomCache(2);
        cache.put("A", "Alpha", 0);
        cache.put("B", "Beta", 0);
        cache.get("A");
        cache.put("C", "Gamma", 0);

        assertNotNull(cache.get("A"));
        assertNull(cache.get("B"));
        assertNotNull(cache.get("C"));
    }

    @Test
    void shouldEvictUsingLfu() {
        CustomCache cache = new CustomCache(2);
        cache.setPolicy("LFU");
        cache.put("A", "Alpha", 0);
        cache.put("B", "Beta", 0);
        cache.get("A");
        cache.get("A");
        cache.put("C", "Gamma", 0);

        assertNotNull(cache.get("A"));
        assertNull(cache.get("B"));
        assertNotNull(cache.get("C"));
    }

    @Test
    void shouldExpireUsingTtl() throws InterruptedException {
        CustomCache cache = new CustomCache(2);
        cache.put("A", "Alpha", 1);

        assertEquals("Alpha", cache.get("A"));
        Thread.sleep(1100);
        assertNull(cache.get("A"));
    }

    @Test
    void shouldTrackHitsAndMisses() {
        CustomCache cache = new CustomCache(2);
        cache.put("A", "Alpha", 0);
        cache.get("A");
        cache.get("Missing");

        CacheMetrics metrics = cache.metrics();
        assertEquals(1, metrics.hits());
        assertEquals(1, metrics.misses());
        assertEquals(50.0, metrics.hitRate(), 0.01);
    }
}
