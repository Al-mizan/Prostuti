package com.prostuti.feature.profile.presentation

import com.prostuti.core.common.UserStats
import com.prostuti.core.model.UserProfileDto

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(
        val profile: UserProfileDto,
        val stats: UserStats = UserStats.Empty,
        val isUpdating: Boolean = false,
        val errorMessage: String? = null,
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

sealed interface ProfileUiEvent {
    data class UpdateName(val name: String) : ProfileUiEvent
    data class UpdateAvatar(val avatarId: String) : ProfileUiEvent
    data object Refresh : ProfileUiEvent
    data object Logout : ProfileUiEvent
}
