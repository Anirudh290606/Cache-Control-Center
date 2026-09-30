package com.acentra.cache;

import java.util.Map;

public interface EvictionPolicy {
    String name();
    String selectVictim(Map<String, CacheEntry> entries);
}
