package com.acentra.cache;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CacheService {
    private final CustomCache cache = new CustomCache(5);

    public void put(String key, String value, long ttlSeconds) {
        cache.put(key, value, ttlSeconds);
    }

    public String get(String key) {
        return cache.get(key);
    }

    public boolean remove(String key) {
        return cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    public Map<String, CacheEntry> entries() {
        return cache.snapshot();
    }

    public CacheMetrics metrics() {
        return cache.metrics();
    }

    public void setPolicy(String policy) {
        cache.setPolicy(policy);
    }

    public String policy() {
        return cache.getPolicy();
    }

    public int capacity() {
        return cache.capacity();
    }

    public String samplePattern() {
        cache.clear();
        cache.put("A", "Alpha", 0);
        cache.put("B", "Beta", 0);
        cache.put("C", "Gamma", 0);
        cache.get("A");
        cache.get("B");
        cache.get("A");
        cache.put("D", "Delta", 0);
        return "Executed: PUT A, PUT B, PUT C, GET A, GET B, GET A, PUT D";
    }

    public String concurrencyTest() {
        cache.clear();
        int threads = 10;
        int operationsPerThread = 100;
        Thread[] workers = new Thread[threads];

        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    String key = "K" + (j % 5);
                    cache.put(key, "V" + j, 0);
                    cache.get(key);
                }
            });
            workers[i].start();
        }

        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Concurrency test interrupted";
            }
        }

        return "Completed " + (threads * operationsPerThread) + " PUT/GET cycles across " + threads + " threads";
    }
}
