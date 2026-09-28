---
name: prostuti-conventions
description: Conventions for the Prostuti KMP course project — a Kotlin Multiplatform, Android-only exam-prep app with an embedded Ktor backend, built for a university Android development course.  Use this whenever writing, reviewing, or scaffolding code in this repo — adding a feature module, writing a Ktor route, defining a Room entity or Exposed table, writing a ViewModel/UiState, wiring Koin DI, handling CSV question import, or touching the Role/Subject/ExamType enums. Check this before proposing a new module, a new pattern, or a new dependency in this codebase.
---

# Prostuti KMP Course Project — Conventions

Read `docs/project_guide.md` for scope and build order, and `docs/backend_design.md` for the full data model, API, and CSV schema. This file is the "how we write code here" reference — keep it open while scaffolding.

## 0. What this project is (say this before doing anything structural)

- **KMP, Android client only.** There is no iOS/desktop target. Multiplatform is used for exactly one thing: sharing `core/model` (DTOs, enums, validation) between the Android app and the Ktor backend, both of which are JVM-based. Don't add other targets "for future-proofing" — that's scope the course project doesn't need.
- **The backend lives inside this same Gradle project**, as a `server` module. It is not a separate repo. That's the literal meaning of "backend in the app structure" here.
- **Question bank = BCS only.** No Bank/Government/NTRCA exam types. Don't add an `examType` enum with multiple values — `examSession` (e.g. "45th BCS Preliminary") is the only variable dimension.
- **No live/real-time exam.** No websockets, no live leaderboard push, no multiplayer battle mode. "Exam Mode" is a solo, timed mock test — that's still in scope, it's just not real-time.
- **Practice subjects are a fixed, closed set of 9** — see §4. Never let a feature imply this list is user-editable.

## 1. Golden rule: no ceremony beyond what the scope needs

This mirrors the same principle already in use on the main Prostuti (Flutter) build: **don't reach for Clean Architecture layering, extra interfaces, or extra Gradle modules "for correctness."** Concretely:

- Only `core/model` needs real KMP targets (`androidTarget()` + `jvm("server")`). Every other module (`core/network`, `core/database`, `core/designsystem`, `feature/*`, `androidApp`) is a **plain Android Kotlin module**. `server` is a **plain Kotlin/JVM module**. Don't multiplatform-ify a module just because the project is "a KMP project."
- One repository implementation per interface is fine without a second implementation "for testability theater" — write the interface because a ViewModel needs to depend on an abstraction for fakes in tests, not because layering demands it.
- Derive data instead of storing it when a query will do (see `backend_design.md` §5 — wrong-answers and leaderboard are both derived, not separately stored).

If you're about to add a module, a layer, or an abstraction, and you can't point to which requirement in `project_guide.md` needs it, don't add it.

## 2. Module structure

```
prostuti-kmp/
├── core/
│   ├── model/          # KMP: androidTarget() + jvm("server") — DTOs, enums, validation
│   ├── network/        # Android module — Ktor client, auth header interceptor
│   ├── database/       # Android module — Room, offline cache only
│   ├── designsystem/   # Android module — Compose theme, colors, shared composables
│   └── common/         # Android module — Result<T>, DataStore session storage, utils
├── feature/
│   ├── auth/
│   ├── questionbank/
│   ├── practice/
│   ├── exam/
│   ├── history/
│   ├── profile/
│   └── admin/           # Role.ADMIN-gated: CSV import, question review, user roles
├── androidApp/           # nav graph, DI wiring, MainActivity
└── server/               # Ktor: routes/ services/ db/ plugins/
```

`core/model` is the only module both `androidApp`'s feature modules and `server` depend on. Nothing else crosses that boundary — the Android app never imports server internals, and the server never imports Android/Compose code.

## 3. Feature module layout

Every module under `feature/` follows the same shape:

```
feature/practice/
├── build.gradle.kts
└── src/main/kotlin/com/prostuti/feature/practice/
    ├── domain/          # use cases, repository interface
    ├── data/            # repository impl, DTO ↔ domain mapping
    ├── presentation/    # ViewModel, UiState, UiEvent
    ├── ui/               # Composable screens, reading only from presentation/
    └── di/               # one Koin module per feature
```

Naming:
- Use case: `VerbNounUseCase` (e.g. `StartPracticeSessionUseCase`)
- Repository interface in `domain/`, impl in `data/` as `XxxRepositoryImpl`
- ViewModel: `XxxViewModel`, one per screen (not per feature)
- UiState: sealed interface, not a single mutable data class with a dozen nullable fields

```kotlin
sealed interface PracticeUiState {
    data object Loading : PracticeUiState
    data class Success(val questions: List<Question>, val index: Int) : PracticeUiState
    data class Error(val message: String) : PracticeUiState
}

class PracticeViewModel(
    private val startSession: StartPracticeSessionUseCase,
    private val submitAnswer: SubmitAnswerUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<PracticeUiState>(PracticeUiState.Loading)
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    fun onEvent(event: PracticeUiEvent) { /* reduce into _uiState */ }
}
```

Screens read `uiState` and dispatch `onEvent` — no business logic in Composables.

## 4. Shared enums (`core/model`) — single source of truth

Never redefine these inside a feature module.

```kotlin
enum class Role { STUDENT, ADMIN }

enum class Subject {
    BENGALI, ENGLISH, BD_INTERNATIONAL_AFFAIRS, GEOGRAPHY,
    SCIENCE, IT, MATH, MENTAL_ABILITY, ETHICS
}

enum class QuestionType { BANK, PRACTICE }   // BANK = BCS question bank, PRACTICE = practice mode
enum class SessionType { PRACTICE, EXAM }     // EXAM = solo timed BCS mock test
```

If a task seems to need a 10th subject or a second exam type, stop and confirm against `project_guide.md` §4 rather than extending the enum — that list is fixed by the course scope.

## 5. Networking & DI conventions

- `core/network` exposes a single configured `HttpClient` (Ktor client, `ContentNegotiation` + `kotlinx.serialization`, auth header injected from `core/common`'s session store). Feature repositories inject this client — don't create a second `HttpClient` per feature.
- Koin: one `module { }` per feature in its `di/` package, registered in `androidApp`'s `startKoin { modules(...) }`. Pattern:

```kotlin
val practiceModule = module {
    single<PracticeRepository> { PracticeRepositoryImpl(get()) }
    factory { StartPracticeSessionUseCase(get()) }
    viewModel { PracticeViewModel(get(), get()) }
}
```

## 6. Backend (`server`) conventions

- Routes are extension functions on `Route`, one file per resource: `routes/PracticeRoutes.kt`, `routes/AdminRoutes.kt`, etc.
- Routes call into a `Service` class (business logic + validation); services call `db/` (Exposed table objects + DAO-style query functions). Routes never touch Exposed directly.
- Role gating happens **server-side**, always — client-side hiding of admin screens is UX only, never the security boundary.

```kotlin
fun Route.adminOnly(build: Route.() -> Unit) = authenticate {
    intercept(ApplicationCallPipeline.Plugins) {
        val role = call.principal<UserPrincipal>()?.role
        if (role != Role.ADMIN) { call.respond(HttpStatusCode.Forbidden); return@intercept finish() }
    }
    build()
}
```

- Schema creation via Exposed's `SchemaUtils` at startup is fine for this project's scale — don't introduce a migration tool (Flyway etc.) unless the schema starts changing after real data exists.
- CSV parsing: use a real CSV parser (e.g. Apache Commons CSV), never manual `split(",")` — question text routinely contains commas. Full column schema is in `backend_design.md` §7; don't invent new columns without updating that doc first.


## 7. Explicitly out of scope — don't build these unless the plan changes

- Live/real-time battle mode (websockets, live rank push)
- Bank/Government/NTRCA question types
- Job circular notifications
- Ad-serving / subscription-alternative monetization system
- A general-purpose "Prostuti AI" chat assistant — only the scoped per-question explanation endpoint (`backend_design.md` §9) is in.

If a request in chat implies building one of these, flag the conflict with `docs/project_guide.md` §3 instead of silently implementing it.