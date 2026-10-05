package com.prostuti.core.network

import com.prostuti.core.common.SessionStore
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Single configured `HttpClient` for the whole Android app — per
 * prostuti-conventions §5, feature repositories must NOT create their own.
 *
 * Auth is handled dynamically by inspecting `SessionStore.token` on every outgoing
 * request inside `defaultRequest`. This guarantees immediate token invalidation on
 * logout and seamless switching between user accounts without in-memory caching.
 */
object HttpClientFactory {

    fun create(apiConfig: ApiConfig, sessionStore: SessionStore): HttpClient =
        HttpClient(CIO) {
            expectSuccess = false
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                })
            }
            install(Logging) {
                level = LogLevel.INFO
            }
            defaultRequest {
                url(apiConfig.baseUrl)
                contentType(ContentType.Application.Json)
                val token = sessionStore.token
                if (!token.isNullOrBlank()) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
}
