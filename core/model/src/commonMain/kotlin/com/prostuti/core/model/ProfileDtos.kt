package com.prostuti.core.model

import kotlinx.serialization.Serializable

/**
 * DTOs for user profile retrieval and updates.
 * Shared between Android client and Ktor server.
 */

@Serializable
data class UserProfileDto(
    val id: String,
    val name: String,
    val email: String,
    val role: Role,
    val avatarId: String,
    val createdAt: String,
)

@Serializable
data class UpdateProfileRequest(
    val name: String,
    val avatarId: String,
)
