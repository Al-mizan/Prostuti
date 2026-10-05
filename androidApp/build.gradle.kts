plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.prostuti.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.prostuti.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val devBaseUrl = (project.findProperty("PROSTUTI_API_BASE_URL") as String?)
            ?: "https://api-prostuti.onrender.com"
        buildConfigField("String", "API_BASE_URL", "\"$devBaseUrl\"")

        val webGoogleClientId = (project.findProperty("WEB_GOOGLE_CLIENT_ID") as String?)
            ?: System.getenv("WEB_GOOGLE_CLIENT_ID")
            ?: "746885731832-ccsjstr7t6tiu8esaujlut4g0msfid25.apps.googleusercontent.com"
        buildConfigField("String", "WEB_GOOGLE_CLIENT_ID", "\"$webGoogleClientId\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Core modules
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))

    // Feature modules
    implementation(project(":feature:auth"))
    implementation(project(":feature:questionbank"))
    implementation(project(":feature:practice"))
    implementation(project(":feature:exam"))
    implementation(project(":feature:history"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:admin"))

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // AndroidX
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Test
    testImplementation(libs.junit)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Google Auth (Credential Manager)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.google.identity.googleid)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
