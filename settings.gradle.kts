pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Prostuti"

// ── Core ────────────────────────────────────────────────────────────
include(":core:model")           // KMP: androidTarget()
include(":core:network")         // Android library
include(":core:database")        // Android library
include(":core:designsystem")    // Android library
include(":core:common")          // Android library

// ── Feature ─────────────────────────────────────────────────────────
include(":feature:auth")
include(":feature:questionbank")
include(":feature:practice")
include(":feature:exam")
include(":feature:history")
include(":feature:profile")
include(":feature:admin")

// ── App ─────────────────────────────────────────────────────────────
include(":androidApp")