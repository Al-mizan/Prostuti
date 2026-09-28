plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.prostuti.core.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    api(project(":core:model"))
    implementation(project(":core:common"))

    // `api` so consumers (androidApp, feature/auth) can refer to HttpClient
    // through Koin's `get()` binding without re-declaring ktor.
    api(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    api(libs.ktor.client.content.negotiation)
    api(libs.ktor.serialization.kotlinx.json)
    api(libs.ktor.client.auth)
    implementation(libs.ktor.client.logging)
}
