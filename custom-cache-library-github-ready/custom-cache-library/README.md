# Custom Cache Library with Live Metrics Panel

A custom, thread-safe in-memory cache built with Java and Spring Boot, with LRU/LFU eviction, per-entry TTL, cache metrics, REST APIs, and a React dashboard.

## Features

- LRU (Least Recently Used) eviction
- LFU (Least Frequently Used) eviction
- Runtime eviction-policy selection
- Per-entry TTL (Time-To-Live)
- Thread-safe GET/PUT/REMOVE/CLEAR operations
- Cache hit/miss metrics
- Eviction metrics
- Sample access-pattern simulation
- Concurrent access test endpoint
- React live metrics dashboard
- No Redis, Caffeine, Guava, or other ready-made cache implementation

## Architecture

```text
React Dashboard
      |
      | REST / JSON
      v
Spring Boot REST API
      |
      v
Cache Service
      |
      v
Custom Cache Engine
  |       |       |
 LRU     LFU     TTL
      |
In-Memory Storage
```

## Requirements

- Java 17+
- Maven 3.8+
- Node.js 18+

## Run the backend

```bash
cd backend
mvn spring-boot:run
```

Backend: `http://localhost:8080`

## Run the frontend

Open another terminal:

```bash
cd frontend
npm install
npm run dev
```

Frontend: `http://localhost:5173`

## Run tests

```bash
cd backend
mvn test
```

## Main API endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| PUT | `/api/cache/{key}` | Put/update an entry |
| GET | `/api/cache/{key}` | Get an entry |
| DELETE | `/api/cache/{key}` | Remove an entry |
| DELETE | `/api/cache` | Clear cache |
| GET | `/api/cache/entries` | List current entries |
| GET | `/api/cache/metrics` | View metrics |
| PUT | `/api/cache/policy/{policy}` | Select LRU or LFU |
| POST | `/api/cache/sample-pattern` | Run sample access pattern |
| POST | `/api/cache/concurrency-test` | Run concurrent access test |

## Example PUT

```bash
curl -X PUT "http://localhost:8080/api/cache/user1?value=Aparna&ttlSeconds=60"
```

## Example GET

```bash
curl "http://localhost:8080/api/cache/user1"
```

## Team Responsibilities

### Member 1 — Cache Engine
- Cache data structure
- LRU
- LFU
- TTL
- Thread safety
- Unit tests

### Member 2 — Backend
- Spring Boot
- REST APIs
- Metrics
- Sample patterns
- Concurrency testing

### Member 3 — Frontend
- React dashboard
- Cache operations
- Policy selector
- Metrics visualization
- Cache-entry table

## Repository hygiene

Do not commit generated folders such as:

```text
target/
node_modules/
dist/
.idea/
.vscode/
```

Do not commit passwords, API keys, tokens, or `.env` files.

## Challenge

Developed as a solution for the Acentra Health Build to Care Codeathon.
