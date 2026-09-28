package com.prostuti.core.network

/**
 * The Android `androidApp` module defines `API_BASE_URL` via
 * `buildConfigField` and passes it through Koin into `HttpClientFactory`.
 * Keeping it as a constructor arg instead of a static constant lets tests
 * swap in a mock host without touching code.
 */
data class ApiConfig(val baseUrl: String) {
    init {
        require(baseUrl.isNotBlank()) { "ApiConfig.baseUrl must not be blank" }
    }
    companion object {
        /**
         * Dev default for the Android emulator's host-loopback alias.
         * On a physical device, the team sets this via the buildConfigField
         * in `androidApp/build.gradle.kts`.
         */
        const val DEFAULT_EMULATOR_BASE_URL = "http://10.0.2.2:5000/"
    }
}
