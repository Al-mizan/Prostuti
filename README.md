# Prostuti (প্রস্তুতি)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-blue.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)

A modern BCS exam-prep application built with Kotlin Multiplatform (Android client) and an embedded Ktor backend service.

---

## 📖 Overview

**Prostuti** is an exam preparation app tailored for BCS aspirants. The repository includes both the modern Jetpack Compose Android client and an embedded Ktor backend module, sharing common models via Kotlin Multiplatform.

### Key Highlights
- **BCS Question Bank**: Browse past BCS questions by exam session and subject.
- **Practice Mode**: Subject-wise practice across 9 core subjects (Bengali, English, Bangladesh & International Affairs, Geography, Science, IT, Math, Mental Ability, Ethics).
- **Exam Mode**: Solo, timed BCS mock tests with automated scoring and instant review.
- **History & Analytics**: Track previous exam attempts, performance analytics, and incorrect answer reviews.
- **Admin Panel**: Built-in role-based admin dashboard with CSV bulk question import, question management, and user role management.
- **Shared Architecture**: Shared data models between Ktor backend and Android client using KMP (`core:model`).

---

## 🏗️ Architecture & Modules

The repository follows a modular, feature-based Gradle structure:

```
Prostuti/
├── core/
│   ├── model/             # KMP shared models (Android + JVM/Server)
│   ├── network/           # Ktor HTTP client & network handling
│   ├── database/          # Local Room database & caching
│   ├── designsystem/      # Prostuti Compose design tokens, colors & UI components
│   └── common/            # Shared utilities & dispatchers
├── feature/
│   ├── auth/              # Authentication (Login / Register)
│   ├── questionbank/      # BCS Question Bank browsing
│   ├── practice/          # Subject-wise practice sessions
│   ├── exam/              # Mock exam & test simulation
│   ├── history/           # Attempt history & performance analysis
│   ├── profile/           # User profile & settings
│   └── admin/             # Role-based admin panel & CSV import
├── androidApp/            # Android application entry point & navigation
├── server/                # Embedded Ktor backend server (JWT auth, Exposed ORM, SQLite)
└── docs/                  # Project specifications, API contracts & guides
```

---

## 🛠️ Tech Stack

- **Client**: Android (Kotlin Multiplatform), Jetpack Compose, Material 3, Navigation Compose, Koin (DI), Room
- **Server**: Ktor Server (Netty), Exposed ORM, SQLite / HikariCP, JWT Auth, BCrypt
- **Shared**: Kotlin Multiplatform (`core:model`), Kotlinx Serialization, Coroutines

---

## 🚀 Getting Started

### Prerequisites
- JDK 17 or higher
- Android Studio (Koala / Ladybug or newer recommended)
- Android SDK (API 34+)

### 1. Running the Server

Start the embedded Ktor backend server:

```bash
./gradlew :server:run
```
The server will start on `http://127.0.0.1:5000` (or `http://localhost:5000`).

### 2. Running on Android Device / Emulator

If running on a physical Android device connected via USB:

```bash
# Bridge port 5000 between host and device
adb reverse tcp:5000 tcp:5000

# Build and install the debug APK
./gradlew :androidApp:installDebug

# Launch the app
adb shell am start -n com.prostuti.app/.MainActivity
```

For Android Emulator, you can configure the backend base URL to `http://10.0.2.2:5000`:
```bash
./gradlew :androidApp:installDebug -PPROSTUTI_API_BASE_URL=http://10.0.2.2:5000
```

---

## 📄 Documentation

Detailed specifications and architectural decisions can be found in the `docs/` directory:
- [Project Guide](docs/project_guide.md)
- [Backend Design & API Contracts](docs/backend_design.md)
- [Software Requirements Specification](docs/srs.md)

---

## 📝 License

Distributed under the MIT License. See `LICENSE` for details.
