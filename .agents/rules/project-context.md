---
trigger: always_on
---

# Prostuti KMP Course Project — Agent Rules

Activation: Always On.

Before starting ANY task in this workspace, read:

- @docs/project_guide.md — scope, feature priority (core/stretch/deferred), build order
- @docs/backend_design.md — data model, API contract, CSV schema, auth design
- @.agents/skills/prostuti-conventions/SKILL.md — module layout, naming, DI/networking patterns, what NOT to build

## Hard constraints — do not violate these even if a prompt seems to imply otherwise

- Android-only KMP client. Only `core/model` is multiplatform (`androidTarget()` + `jvm("server")`). Every other module is a plain Android module or a plain JVM module — do not add multiplatform targets elsewhere.
- Question bank is BCS only. Never add Bank/Government/NTRCA exam types.
- No live/real-time exam. No websockets, no live leaderboard push, no multiplayer battle mode.
- Practice subjects are the fixed 9 listed in docs/project_guide.md §4. Never add, remove, or rename entries in the `Subject` enum without being explicitly told to.
- Role checks are server-side and non-negotiable. Client-side hiding of admin screens is UX only, never the security boundary.
- Reuse existing modules and components before creating new ones. If a task seems to require a new core module, a new architectural pattern, or a new dependency not already named in the SKILL.md, stop and flag it in your plan instead of silently adding it.
- Anything listed under "Deferred" in docs/project_guide.md §3 or SKILL.md §8 is out of scope unless a prompt explicitly says otherwise.
