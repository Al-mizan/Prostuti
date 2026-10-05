# ADR 0002: Embedded Ktor Server and Single Android Client KMP Structure

- **Status:** Superseded by ADR 0003
- **Date:** 2026-08-26 (Superseded: 2026-10-05)
- **Authors:** Prostuti Team
- **Tags:** architecture, kmp, ktor, compose, modularity

## Context and Problem Statement

The initial course requirements specified:
1. Kotlin Multiplatform build.
2. Backend included inside the app's project structure.
3. Role-based admin panel built into the app.
4. Android client only (no desktop/iOS builds).

We needed an initial architecture that fulfilled these constraints without unnecessary ceremony or complexity.

## Decision Drivers

- "No ceremony beyond what the scope needs".
- Single language ecosystem (Kotlin everywhere).
- Shared DTOs and validation between backend and client.
- Fast local and remote development setup.

## Decision Outcome

We initially decided on:
1. **Multiplatform Target**: Only `core/model` is multiplatform (`androidTarget()` + `jvm("server")`). It contains shared DTOs, enums (`Role`, `Subject`, `QuestionType`, `SessionType`), and validation logic.
2. **Backend**: A plain Kotlin/JVM module (`server`) running Ktor, Exposed, and PostgreSQL (Neon remote database with SSL).
3. **Android Client**: Standard Android Gradle modules (`androidApp`, `feature/*`, `core/network`, `core/database`, `core/designsystem`, `core/common`) utilizing Jetpack Compose and Koin DI.
4. **Offline Cache**: Android-side Room DB (`core/database`) for downloaded question sets.

### Positive Consequences

- Zero build overhead from multiplatform targets on client-only UI/feature modules.
- Single source of truth for DTOs and API contracts in initial phase.
- Embedded server allowed running and grading the application within one repository.

### Negative Consequences / Trade-offs

- Tightly coupled the backend build and dependencies to Android Gradle and JVM tooling.
- Prevented leveraging modern TypeScript / Node.js backend ecosystem tools and standalone service deployment.
- Led to ADR 0003.
