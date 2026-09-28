# ADR 0002: Embedded Ktor Server and Single Android Client KMP Structure

- **Status:** Accepted
- **Date:** 2026-08-26
- **Authors:** Prostuti Team
- **Tags:** architecture, kmp, ktor, compose, modularity

## Context and Problem Statement

The university Android development course requires:
1. Kotlin Multiplatform build.
2. Backend included inside the app's project structure.
3. Role-based admin panel built into the app.
4. Android client only (no desktop/iOS builds).

We need an architecture that fulfills these constraints without unnecessary ceremony or complexity.

## Decision Drivers

- "No ceremony beyond what the scope needs" (SKILL.md §1).
- Single language ecosystem (Kotlin everywhere).
- Shared DTOs and validation between backend and client.
- Fast local and remote development setup.

## Decision Outcome

We decided on:
1. **Multiplatform Target**: Only `core/model` is multiplatform (`androidTarget()` + `jvm("server")`). It contains all shared DTOs, enums (`Role`, `Subject`, `QuestionType`, `SessionType`), and validation logic.
2. **Backend**: A plain Kotlin/JVM module (`server`) running Ktor, Exposed, and PostgreSQL (Neon remote database with SSL).
3. **Android Client**: Standard Android Gradle modules (`androidApp`, `feature/*`, `core/network`, `core/database`, `core/designsystem`, `core/common`) utilizing Jetpack Compose and Koin DI.
4. **Offline Cache**: Android-side Room DB (`core/database`) for downloaded question sets.

### Positive Consequences

- Zero build overhead from multiplatform targets on client-only UI/feature modules.
- Single source of truth for DTOs and API contracts.
- Embedded server allows running and grading the full application within one repository.

### Negative Consequences / Trade-offs

- Does not produce iOS or Desktop binaries (explicitly intentional per course scoping).
