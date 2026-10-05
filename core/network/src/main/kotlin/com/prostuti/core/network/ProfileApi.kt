package com.prostuti.core.network

import com.prostuti.core.model.ApiResponse
import com.prostuti.core.model.UpdateProfileRequest
import com.prostuti.core.model.UserProfileDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Network API client for user profile retrieval and updates.
 */
class ProfileApi(private val client: HttpClient) {

    suspend fun getProfile(): UserProfileDto {
        val response = client.get("api/v1/profile")
        val envelope = response.body<ApiResponse<UserProfileDto>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }

    suspend fun updateProfile(request: UpdateProfileRequest): UserProfileDto {
        val response = client.put("api/v1/profile") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val envelope = response.body<ApiResponse<UserProfileDto>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }
}
