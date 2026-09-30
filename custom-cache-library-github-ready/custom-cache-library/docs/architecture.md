# Architecture

## Request flow

```text
React
  |
  | HTTP/JSON
  v
CacheController
  |
  v
CacheService
  |
  v
CustomCache
  |------> LruEvictionPolicy
  |
  |------> LfuEvictionPolicy
  |
  `------> CacheEntry / TTL / Metrics
```

## Design

The cache engine owns cache state and synchronization. Eviction is isolated behind the `EvictionPolicy` interface, allowing additional strategies to be introduced without changing the main cache API.

All public cache operations are synchronized to keep the implementation safe for concurrent access.
