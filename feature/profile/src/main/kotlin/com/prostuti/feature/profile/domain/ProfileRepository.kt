package com.prostuti.feature.profile.domain

import com.prostuti.core.common.Result
import com.prostuti.core.model.UpdateProfileRequest
import com.prostuti.core.model.UserProfileDto

interface ProfileRepository {
    suspend fun getProfile(): Result<UserProfileDto>
    suspend fun updateProfile(request: UpdateProfileRequest): Result<UserProfileDto>
    fun clearSession()
}
