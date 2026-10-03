package com.prostuti.core.network

import com.prostuti.core.model.AuthResponse
import com.prostuti.core.model.BootstrapAdminRequest
import com.prostuti.core.model.GoogleAuthRequest
import com.prostuti.core.model.LoginRequest
import com.prostuti.core.model.MeResponse
import com.prostuti.core.model.RegisterRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * The only network surface a feature repository should need to talk to
 * the server's auth endpoints. Routes:
 *   POST /api/v1/auth/register
 *   POST /api/v1/auth/login
 *   POST /api/v1/auth/google
 *   POST /api/v1/auth/bootstrap-admin   (dev-time; client never calls this)
 *   GET  /api/v1/me                     (Bearer-protected; proves token attach)
 */
class AuthApi(private val client: HttpClient) {

    suspend fun register(name: String, email: String, password: String): AuthResponse =
        client.post("api/v1/auth/register") {
            setBody(RegisterRequest(name, email, password))
        }.body()

    suspend fun login(email: String, password: String): AuthResponse =
        client.post("api/v1/auth/login") {
            setBody(LoginRequest(email, password))
        }.body()

    suspend fun loginWithGoogle(idToken: String): AuthResponse =
        client.post("api/v1/auth/google") {
            setBody(GoogleAuthRequest(idToken))
        }.body()

    suspend fun bootstrapAdmin(
        token: String,
        name: String,
        email: String,
        password: String,
    ): AuthResponse =
        client.post("api/v1/auth/bootstrap-admin") {
            header("X-Bootstrap-Token", token)
            setBody(BootstrapAdminRequest(name, email, password))
        }.body()

    suspend fun me(): MeResponse = client.get("api/v1/me").body()
}
