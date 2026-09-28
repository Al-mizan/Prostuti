package com.prostuti.core.network

import com.prostuti.core.common.SessionStore
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
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
 * Auth is handled by `Auth` + `bearer` reading the JWT from `SessionStore`
 * on every outgoing request. The block runs at request time, so a
 * `login()` → next call correctly picks up the freshly stored token
 * without rebuilding the client.
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
            install(Auth) {
                bearer {
                    loadTokens {
                        val t = sessionStore.token
                        if (t.isNullOrBlank()) null else BearerTokens(t, "")
                    }
                    // We don't send refresh tokens in this scope; a missing token
                    // means the request is unauthenticated. The server decides.
                    refreshTokens {
                        null
                    }
                    sendWithoutRequest { req ->
                        // Send on /api/v1/* — every request hits the API surface.
                        // Public endpoints (register/login/bootstrap) ignore the header.
                        req.url.encodedPath.startsWith("/api/v1/")
                    }
                }
            }
            defaultRequest {
                url(apiConfig.baseUrl)
                contentType(ContentType.Application.Json)
            }
        }
}
