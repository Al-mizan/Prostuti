# Domain Context: Prostuti (KMP Course Project)

This document establishes the **ubiquitous language**, domain glossary, and context boundaries for the Prostuti KMP project. All code, tickets, specs, and discussions should strictly use these terms.

---

## 1. Ubiquitous Language & Core Entities

### User & Authentication
- **User**: An authenticated user of the system with an assigned `Role`.
- **Role**:
  - `STUDENT`: Regular user who can practice subjects, take BCS mock exams, view history, and view leaderboards.
  - `ADMIN`: Privileged user who can import questions via CSV, review/CRUD questions, and manage user roles.
- **JWT (JSON Web Token)**: Stateless bearer token issued upon `/auth/login` and `/auth/register` carrying `sub` (User ID) and `role` claim.

### Content & Question Bank
- **Question**: A multiple-choice question (MCQ) containing `question_text`, four options (`option_a`, `option_b`, `option_c`, `option_d`), `correct_option` (`A`, `B`, `C`, `D`), optional `explanation`, and `difficulty` (`EASY`, `MEDIUM`, `HARD`).
- **QuestionType**:
  - `BANK`: BCS previous years' question bank questions, organized by `exam_session` and `subject`.
  - `PRACTICE`: Subject and topic-based practice questions.
- **Exam Session**: A specific BCS exam batch identifier (e.g., `"45th BCS Preliminary"`). Relevant exclusively to BCS question bank questions and exam mock tests.
- **Subject**: A closed, immutable enum of exactly 9 practice subjects:
  1. `BENGALI`
  2. `ENGLISH`
  3. `BD_INTERNATIONAL_AFFAIRS`
  4. `GEOGRAPHY`
  5. `SCIENCE`
  6. `IT`
  7. `MATH`
  8. `MENTAL_ABILITY`
  9. `ETHICS`
  *(Note: Never add, remove, or rename subjects.)*
- **Topic**: A sub-categorization within a subject (e.g., "সন্ধি" in Bengali, "বীজগণিত" in Math).

### Sessions & Attempts
- **Practice Session (`practice_sessions`)**: A single-subject study session where a student answers a set of questions sequentially.
- **Exam Attempt (`exam_attempts`)**: A timed, solo BCS mock test session for a specific `exam_session` containing questions across BCS subjects, submitted and scored as a batch.
- **Answer (`answers`)**: A recorded response from a user to a specific question within a `SessionType` (`PRACTICE` or `EXAM`), capturing the selected option and whether it was correct (`is_correct`).
- **History**: Derived records of past practice sessions, exam attempts, and wrong answers.
- **Leaderboard**: Derived ranking per `exam_session` based on `(score DESC, time_taken_seconds ASC)`.

---

## 2. Architecture & Bounded Contexts

> For the comprehensive multi-context system architecture and DDD context map, see [root CONTEXT.md](file:///home/almizan/Other Locations/workspace/Projects/hobby/prostuti/prostuti_app/CONTEXT.md) and [CONTEXT-MAP.md](file:///home/almizan/Other Locations/workspace/Projects/hobby/prostuti/prostuti_app/CONTEXT-MAP.md). See also [ADR 0003](file:///home/almizan/Other Locations/workspace/Projects/hobby/prostuti/prostuti_app/docs/adr/0003-migrate-to-standalone-typescript-express-backend.md).

```
┌─────────────────────────────────────────────────────────────┐
│                     Client (Android)                        │
│  androidApp                                                 │
│  ├── feature/auth           (Login, Register, Google SSO)   │
│  ├── feature/questionbank   (BCS Bank Browsing)             │
│  ├── feature/practice       (9-Subject Practice Loop)       │
│  ├── feature/exam           (Timed BCS Mock Test)           │
│  ├── feature/history        (Attempts & Wrong Answers)      │
│  ├── feature/profile        (User Info & Avatar)            │
│  └── feature/admin          (CSV Import, Question/User Mgmt)│
│                                                             │
│  core/network       core/database      core/designsystem    │
│  (ApiResponse<T>    (Room Offline DB)  (Compose Theme)      │
│   Adapter)                                                  │
│  core/common (Session, Result<T>)                           │
└──────────────────────────────┬──────────────────────────────┘
                               │ HTTPS / JSON REST (/api/v1)
                               │ Standard Envelope: { success, message, data, meta }
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                 Standalone Backend API (/backend)           │
│  Node.js 22+, TypeScript 5.9, Express 5.2, Prisma 7         │
│  ├── Passport.js JWT Bearer & Google OAuth                  │
│  ├── Neon PostgreSQL (users, questions, sessions, answers)  │
│  ├── In-Memory Streaming CSV Parsing (busboy + csv-parse)   │
│  └── In-Memory / Redis Caching                              │
└─────────────────────────────────────────────────────────────┘
```

### Module Boundaries & Invariants
1. **Response Envelope Adapter**: `core/network` unwraps the uniform `{ success, message, data, meta }` response envelope and throws typed `ApiException` on error, isolating domain features from transport details.
2. **Server-side Security Boundary**: Role verification (`Role.ADMIN`) is strictly enforced on the backend API. Client-side navigation visibility is UX only.
3. **Derived Entities**: `wrong_answers` and `leaderboard` are computed via SQL queries, never stored in redundant tables.
4. **Offline Cache**: Android Room database (`core/database`) caches downloaded question sets for offline resilience.

---

## 3. Scope Boundaries & Anti-Patterns

### Explicitly In-Scope (Core)
- BCS Question Bank browsing by session and subject.
- Practice mode across the 9 fixed subjects.
- Solo timed BCS Mock Exam mode.
- Past attempt history and derived wrong answer reviews.
- Per-exam leaderboard ranking.
- Role-based Admin panel for CSV question import & question CRUD.
- On-demand AI explanation for MCQs.

### Explicitly Out-of-Scope (Deferred / Prohibited)
- **NO** live or multiplayer real-time battles / WebSockets.
- **NO** non-BCS exam types (no Bank, Government, NTRCA).
- **NO** dynamic or editable subject list.
- **NO** arbitrary Clean Architecture layer duplication or over-abstraction.
