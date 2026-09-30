package com.acentra.cache;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cache")
@CrossOrigin(origins = "http://localhost:5173")
public class CacheController {
    private final CacheService service;

    public CacheController(CacheService service) {
        this.service = service;
    }

    @PutMapping("/{key}")
    public ResponseEntity<String> put(
            @PathVariable String key,
            @RequestParam String value,
            @RequestParam(defaultValue = "0") long ttlSeconds) {
        service.put(key, value, ttlSeconds);
        return ResponseEntity.ok("Stored key: " + key);
    }

    @GetMapping("/{key}")
    public ResponseEntity<String> get(@PathVariable String key) {
        String value = service.get(key);
        return value == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(value);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<String> remove(@PathVariable String key) {
        return service.remove(key)
                ? ResponseEntity.ok("Removed key: " + key)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity<String> clear() {
        service.clear();
        return ResponseEntity.ok("Cache cleared");
    }

    @GetMapping("/entries")
    public Map<String, CacheEntry> entries() {
        return service.entries();
    }

    @GetMapping("/metrics")
    public CacheMetrics metrics() {
        return service.metrics();
    }

    @GetMapping("/policy")
    public Map<String, Object> policy() {
        return Map.of("policy", service.policy(), "capacity", service.capacity());
    }

    @PutMapping("/policy/{policy}")
    public ResponseEntity<String> setPolicy(@PathVariable String policy) {
        try {
            service.setPolicy(policy);
            return ResponseEntity.ok("Policy changed to " + service.policy());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/sample-pattern")
    public ResponseEntity<String> samplePattern() {
        return ResponseEntity.ok(service.samplePattern());
    }

    @PostMapping("/concurrency-test")
    public ResponseEntity<String> concurrencyTest() {
        return ResponseEntity.ok(service.concurrencyTest());
    }
}
