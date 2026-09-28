# ADR 0001: Record Architecture Decisions

- **Status:** Accepted
- **Date:** 2026-08-26
- **Authors:** Prostuti Team
- **Tags:** process, architecture, documentation

## Context and Problem Statement

As the Prostuti KMP course project develops, architectural decisions need to be documented with clear rationale, tradeoffs, and scope boundaries so contributors and AI agents stay aligned with project constraints.

## Decision Drivers

- Course rubric requirements and deadline scoping.
- Clarity on technical choices (KMP boundaries, embedded server, PostgreSQL/Neon, auth).
- Preventing accidental scope creep or over-engineering.

## Decision Outcome

We will use Architectural Decision Records (ADRs) stored under `docs/adr/` in the repository, numbered sequentially starting from `0001`.

### Positive Consequences

- Decisions are explicit, traceable, and easily reviewed.
- Clear history of architectural evolution and constraints.

### Negative Consequences / Trade-offs

- Minor overhead to write and maintain ADRs when making foundational changes.
