# ADR 0003: Migrate to Standalone TypeScript Express & Prisma Backend with Uniform Response Envelopes

- **Status:** Accepted
- **Date:** 2026-10-05
- **Authors:** Prostuti Team
- **Tags:** architecture, backend, typescript, express, prisma, api-envelope, passport, migration
- **Supersedes:** ADR 0002

## Context and Problem Statement

Initially, Prostuti ran an embedded Ktor server inside the Android Gradle tree (`app/server`) per ADR 0002. However, several architectural limitations emerged:
1. **Monolithic Backend Coupling:** Bundling the backend into the Gradle build system complicated deployment, independent scaling, and containerization.
2. **Production Blueprint Alignment:** The project established a comprehensive production-grade blueprint ([`backend/docs/backend.md`](file:///home/almizan/Other%20Locations/workspace/Projects/hobby/prostuti/prostuti_app/backend/docs/backend.md)) utilizing Node.js, Express, TypeScript, and Prisma.
3. **Response Envelope Standardization:** The Ktor backend emitted unwrapped DTOs, whereas production standards require uniform envelopes `{ success, message, data, meta }` and structured error handling.
4. **Auth & Ingestion Specifics:** Mobile clients require Bearer JWT and Google OAuth tokens, and admin content imports require high-throughput in-memory CSV parsing (`busboy`) rather than disk-heavy or third-party storage dependencies.

## Decision Drivers

- Decouple the backend service from Android Gradle tools for independent deployment and modern developer experience.
- Enforce strict, type-safe API communication between backend and mobile clients via uniform envelopes.
- Eliminate bloat dependencies (prune Stripe, Socket.IO, PDFKit, and Cloudinary).
- Maintain existing Neon PostgreSQL database data integrity.

## Considered Options

1. **Retain Embedded Ktor (`app/server`)**: Stay with JVM/Gradle-based Ktor server.
2. **Migrate to Standalone TypeScript + Express + Prisma in `/backend`**: Extract the backend into a standalone service conforming to `backend/docs/backend.md` and synchronize the Android client's network layer.

## Decision Outcome

Chosen Option: **Migrate to Standalone TypeScript + Express + Prisma in `/backend` (Option 2)**.

### Architectural Changes:
1. **Standalone Backend (`/backend`):**
   - Node.js 22+, TypeScript 5.9, Express 5.2, Prisma 7 with PostgreSQL adapter.
   - Multi-file Prisma schema under `prisma/schema/` mapping to existing Neon PostgreSQL tables (`users`, `questions`, `practice_sessions`, `exam_attempts`, `answers`).
   - Authentication via Passport.js (`passport-jwt` for Bearer auth and Google OAuth token endpoint).
   - In-memory streaming CSV parsing with `busboy` and `csv-parse` for admin question ingestion (zero disk writes).
   - All endpoints served under `/api/v1` wrapped in uniform `{ success, message, data, meta }` envelopes.
2. **Android Client Adaptation (`app/`):**
   - Introduce `ApiResponse<T>` and `MetaDto` in `app/core/model`.
   - Update `app/core/network` HTTP client to unwrap envelopes and throw strongly-typed `ApiException` upon errors.
   - Preserve existing domain use cases and UI screens in `app/feature/*` with zero regressions.

### Positive Consequences

- Clean separation of concerns between client and server bounded contexts.
- Independent CI/CD, deployment, and scaling for the backend.
- Uniform API response envelopes simplify client error handling and pagination across all features.
- In-memory CSV streaming prevents filesystem accumulation and reduces cold storage dependencies.

### Negative Consequences / Trade-offs

- Cross-boundary DTO changes must be manually coordinated across Kotlin and TypeScript rather than compiled from a shared multiplatform module.
- Requires running both the Node.js runtime and Android Gradle build during local full-stack development.
