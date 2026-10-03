package com.prostuti.feature.auth.data

import com.prostuti.core.common.Result
import com.prostuti.core.common.SessionStore
import com.prostuti.core.model.AuthResponse
import com.prostuti.core.model.MeResponse
import com.prostuti.core.model.Role
import com.prostuti.core.network.AuthApi
import com.prostuti.feature.auth.domain.AuthRepository
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.serialization.SerializationException

/**
 * Translates the network-layer auth API into a domain-friendly result.
 * Maps HTTP errors to a readable message; keeps the JWT/role in SessionStore.
 */
class AuthRepositoryImpl(
    private val api: AuthApi,
    private val sessionStore: SessionStore,
) : AuthRepository {

    override suspend fun register(name: String, email: String, password: String): Result<AuthResponse> =
        runAuthCall { api.register(name, email, password) }

    override suspend fun login(email: String, password: String): Result<AuthResponse> =
        runAuthCall { api.login(email, password) }

    override suspend fun loginWithGoogle(idToken: String): Result<AuthResponse> =
        runAuthCall { api.loginWithGoogle(idToken) }

    override suspend fun me(): Result<MeResponse> = try {
        Result.Success(api.me())
    } catch (e: ClientRequestException) {
        if (e.response.status.value == 401) {
            sessionStore.clear()
            Result.Error("Session expired. Please log in again.")
        } else {
            Result.Error(humanize(e))
        }
    } catch (e: ServerResponseException) {
        Result.Error("Server error: ${e.response.status.value}")
    } catch (e: SerializationException) {
        Result.Error("Unexpected response from server")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Network error")
    }

    override fun clearSession() = sessionStore.clear()
    override fun hasSession(): Boolean = sessionStore.hasSession()
    override fun currentRole(): Role? = sessionStore.role

    private suspend fun runAuthCall(block: suspend () -> AuthResponse): Result<AuthResponse> = try {
        val response = block()
        sessionStore.save(response)
        Result.Success(response)
    } catch (e: ClientRequestException) {
        Result.Error(humanize(e))
    } catch (e: ServerResponseException) {
        Result.Error("Server error: ${e.response.status.value}")
    } catch (e: SerializationException) {
        Result.Error("Unexpected response from server")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Network error")
    }

    private fun humanize(e: ClientRequestException): String = when (e.response.status.value) {
        400 -> "Please check your input and try again"
        401 -> "Invalid email or password"
        409 -> "An account with that email already exists"
        else -> "Request failed: ${e.response.status.value}"
    }
}
