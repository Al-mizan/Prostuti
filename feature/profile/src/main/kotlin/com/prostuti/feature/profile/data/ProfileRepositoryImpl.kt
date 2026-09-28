package com.prostuti.feature.profile.data

import com.prostuti.core.common.Result
import com.prostuti.core.common.SessionStore
import com.prostuti.core.model.UpdateProfileRequest
import com.prostuti.core.model.UserProfileDto
import com.prostuti.core.network.ProfileApi
import com.prostuti.feature.profile.domain.ProfileRepository
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.serialization.SerializationException

class ProfileRepositoryImpl(
    private val api: ProfileApi,
    private val sessionStore: SessionStore,
) : ProfileRepository {

    override suspend fun getProfile(): Result<UserProfileDto> = try {
        val profile = api.getProfile()
        Result.Success(profile)
    } catch (e: ClientRequestException) {
        if (e.response.status.value == 401) {
            sessionStore.clear()
            Result.Error("Session expired. Please log in again.")
        } else {
            Result.Error("Failed to fetch profile (${e.response.status.value})")
        }
    } catch (e: ServerResponseException) {
        Result.Error("Server error: ${e.response.status.value}")
    } catch (e: SerializationException) {
        Result.Error("Unexpected response format from server")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Network error occurred")
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): Result<UserProfileDto> = try {
        val profile = api.updateProfile(request)
        Result.Success(profile)
    } catch (e: ClientRequestException) {
        if (e.response.status.value == 401) {
            sessionStore.clear()
            Result.Error("Session expired. Please log in again.")
        } else {
            Result.Error("Failed to update profile (${e.response.status.value})")
        }
    } catch (e: ServerResponseException) {
        Result.Error("Server error: ${e.response.status.value}")
    } catch (e: SerializationException) {
        Result.Error("Unexpected response format from server")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Network error occurred")
    }

    override fun clearSession() {
        sessionStore.clear()
    }
}
