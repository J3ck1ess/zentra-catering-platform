# Zentra — Catering SaaS Management Platform

[![Backend CI](https://github.com/J3ck1ess/zentra-catering-platform/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/J3ck1ess/zentra-catering-platform/actions/workflows/ci.yml)
[![Java 21](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![GHCR](https://img.shields.io/badge/GHCR-published-181717?logo=github&logoColor=white)](https://github.com/J3ck1ess/zentra-catering-platform/pkgs/container/zentra-catering-platform)

Zentra is a multi-module catering SaaS platform built with Spring Boot. It focuses on the backend concerns expected in a production-oriented business system: tenant isolation, JWT and RBAC authorization, transactional ordering, Redis-backed reliability controls, automated testing, and a CI/CD delivery pipeline.

## Highlights

| Area | What Zentra provides |
| --- | --- |
| SaaS design | Merchant-scoped data isolation, DTO-based APIs, dynamic queries, and layered Controller → Service → Mapper architecture |
| Business modules | Employee administration, categories, dishes, users, user administration, and a full order lifecycle |
| Security | Separate `USER` / `EMPLOYEE` identities, JWT authentication, RBAC permissions, request-scoped contexts, and BCrypt passwords |
| Reliability | Transactions, server-side pricing, Redis cache-aside, cache-penetration protection, distributed locking, idempotency, and scheduled order expiry handling |
| Quality | 261 automated tests across unit, web, and integration layers; real MySQL and Redis are used by integration tests |
| Delivery | GitHub Actions quality gate, Docker image build validation, published GHCR images, and environment-ready Staging / Production Compose files |

## Architecture

```text
Client
  ↓
Controller ── validation / OpenAPI / audit annotations
  ↓
Service ───── business rules / transactions / cache governance
  ↓
Mapper ────── MyBatis dynamic SQL
  ↓
MySQL                 Redis
```

Cross-cutting request processing:

```text
JWT interceptor → AuthContext → RBAC permission interceptor → Controller
```

## Functional Modules

| Module | Key capabilities |
| --- | --- |
| Employee & Admin | Employee login, CRUD, status management, self-protection, last-super-admin protection, and permission-based administration |
| Category & Dish | Tenant-aware CRUD, dynamic filtering, category/dish cache-aside, cache eviction, and category deletion constraints |
| User | Registration, login, verification-code and rate-limit safeguards, profile caching, logout / token blacklist, and order history |
| User Administration | User pagination, fuzzy search, status governance, and protected admin APIs |
| Order | Transactional multi-item creation, price snapshots, server-side amounts, state transitions, payment/cancellation, expiry scheduling, locks, and idempotency |
| Admin Frontend | React + Vite administrative UI with JWT storage, route guards, dashboard, and management screens |

## Security and Runtime Governance

### Authentication and authorization

- JWT-based authentication supports independent `USER` and `EMPLOYEE` identities.
- `JwtTokenInterceptor` validates tokens and establishes request-scoped authentication context.
- Annotation-driven RBAC resolves permissions through a centralized role-permission matrix.
- USER and EMPLOYEE API domains are isolated in both directions and verified by integration tests.
- BCrypt protects stored passwords; Redis supports login rate limiting, verification retries, and JWT blacklist checks.

### Redis-backed safeguards

- Cache-aside caching for categories, dishes, and user profiles, including targeted eviction after mutations.
- Empty-value protection for user detail cache penetration.
- Distributed lock protection for concurrent order submission.
- Redis `SETNX` idempotency based on request fingerprints to reject duplicate order creation.

### Order lifecycle

```text
PENDING ── pay ──▶ PAID ── complete ──▶ COMPLETED
   │
   └── cancel / expire ──▶ CANCELLED
```

Orders use a transaction for order and order-item persistence, validate dish availability and tenant ownership, and calculate all amounts on the server.

## Tech Stack

| Layer | Technology |
| --- | --- |
| Backend | Java 21, Spring Boot 3.x, Maven multi-module build |
| Persistence | MySQL, MyBatis, XML dynamic SQL |
| Distributed runtime | Redis, Docker, Docker Compose |
| Security | JWT, RBAC, Spring AOP, BCrypt, Jakarta Validation |
| API documentation | SpringDoc / Swagger OpenAPI 3 |
| Testing | JUnit 5, Mockito, AssertJ, Spring MockMvc |
| Delivery | GitHub Actions, GitHub Container Registry (GHCR) |
| Frontend | React 19, Vite, Tailwind CSS, Ant Design |

## Testing and Quality

| Test layer | Scope | Tests |
| --- | --- | ---: |
| Unit | Service business rules, mapper interactions, cache behavior, validation, and error handling | 132 |
| Web | Controller contracts, request binding, validation, JSON responses, and service delegation | 90 |
| Integration | Real Spring context, MySQL, Redis, MockMvc, JWT, RBAC, caching, and order flows | 39 |
| **Total** | **Latest full Maven suite** | **261** |

The integration suite validates:

- Spring context startup plus MySQL and Redis connectivity.
- Employee and user authentication, expired/invalid token handling, RBAC, and bidirectional USER / EMPLOYEE API identity isolation.
- Category, dish, user, and order persistence flows with real database and Redis interactions.
- Cache hits, misses, eviction, cache-penetration protection, distributed duplicate-order protection, and order status transitions.

Run the full suite locally:

```bash
mvn clean test
```

## CI/CD

Zentra uses a production-oriented CI/CD pipeline that protects `master` and produces versioned, deployable container images.

```text
Feature branch
  ↓
Pull request
  ↓
Backend CI
  ├── Java 21 + Maven dependency cache
  ├── MySQL 8.4 + Redis 7.4 service containers
  ├── Deterministic zentra_test initialization
  ├── Full Maven test suite
  ├── Maven package
  └── Docker image build validation
  ↓
Required PR quality gate
  ↓
Merge to master
  ↓
Publish Container Image → GitHub Container Registry
```

The required `Build, test, and validate container image` check must pass before a pull request can merge into `master`. CI initializes its own `zentra_test` database from `docker/mysql/init.sql`, so results do not depend on a developer machine.

After successful CI on `master`, the release workflow publishes:

```text
ghcr.io/j3ck1ess/zentra-catering-platform:sha-<commit-sha>
ghcr.io/j3ck1ess/zentra-catering-platform:latest
```

Use the immutable SHA tag for deployment and rollback; `latest` is a convenient current-build reference. The project currently implements Continuous Integration and Continuous Delivery: approved changes are validated and published automatically. Automatic deployment is intentionally deferred until a real target server and credentials are available.

## Local Development

### Prerequisites

- JDK 21
- Maven 3.9+
- Docker Desktop (for local infrastructure and integration tests)

### Run with Docker Compose

```bash
docker build -t zentra-server:1.0 .
docker compose up -d
```

| Service | Address |
| --- | --- |
| Zentra API | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| MySQL | `localhost:3307` |
| Redis | `localhost:6380` |

Stop the local stack:

```bash
docker compose down
```

## Staging and Production Configuration

Separate Compose definitions keep environment data, volumes, and runtime configuration isolated:

| Environment | Compose file | Spring profile | Default API port |
| --- | --- | --- | ---: |
| Staging | `docker-compose.staging.yml` | `staging` | 8081 |
| Production | `docker-compose.prod.yml` | `prod` | 8080 |

Real credentials remain outside Git. Copy the appropriate committed template, supply a strong database password, and point `ZENTRA_IMAGE` to an immutable published SHA tag:

```bash
cp .env.staging.example .env.staging
# Edit .env.staging before use.
docker compose --env-file .env.staging -f docker-compose.staging.yml pull
docker compose --env-file .env.staging -f docker-compose.staging.yml up -d
```

Production follows the same process with `.env.prod` and `docker-compose.prod.yml` after an explicitly approved release image has been chosen.

## Observability and API Documentation

- Structured runtime logs use domain prefixes such as `AUTH`, `RBAC`, `CACHE`, `LOCK`, `ORDER`, `VALIDATION`, and `BUSINESS`.
- `@AuditLog` and Spring AOP record business-operation success, failure, operator context, and elapsed time.
- Swagger UI exposes grouped `user-api` and `admin-api` documentation with reusable response schemas and JWT support.

## Project Structure

```text
zentra-catering-platform
├── zentra-common    # Shared auth, context, constants, and Result wrapper
├── zentra-api       # API contracts and DTOs
├── zentra-server    # Spring Boot application, business modules, and tests
├── zentra-admin     # React administrative frontend
├── docker            # MySQL initialization and container support
└── .github/workflows
    ├── ci.yml        # Pull-request and master quality gate
    └── release.yml   # GHCR image publishing
```

## Roadmap

- Deploy the existing environment-ready Compose stack to a real server.
- Add user-facing frontend workflows and password-reset support.
- Expand payment integration and operational monitoring.
- Evaluate Redisson watchdog renewal and automated tenant injection as the system grows.

## Project Goals

Zentra is a backend architecture practice project designed to demonstrate maintainable SaaS development: clear layers, secure identity boundaries, transactional business behavior, distributed-runtime safeguards, automated quality checks, and a delivery path that is ready for real infrastructure.
