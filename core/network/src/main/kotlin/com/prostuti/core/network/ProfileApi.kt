package com.prostuti.core.network

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

    suspend fun getProfile(): UserProfileDto =
        client.get("api/v1/profile").body()

    suspend fun updateProfile(request: UpdateProfileRequest): UserProfileDto =
        client.put("api/v1/profile") {
            setBody(request)
        }.body()
}
