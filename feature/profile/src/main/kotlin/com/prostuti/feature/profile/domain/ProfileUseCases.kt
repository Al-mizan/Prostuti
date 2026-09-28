package com.prostuti.feature.profile.domain

import com.prostuti.core.common.Result
import com.prostuti.core.model.UpdateProfileRequest
import com.prostuti.core.model.UserProfileDto

class GetProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(): Result<UserProfileDto> = repository.getProfile()
}

class UpdateProfileUseCase(private val repository: ProfileRepository) {
    suspend operator fun invoke(name: String, avatarId: String): Result<UserProfileDto> {
        if (name.isBlank()) {
            return Result.Error("Name must not be blank")
        }
        return repository.updateProfile(UpdateProfileRequest(name = name.trim(), avatarId = avatarId))
    }
}
