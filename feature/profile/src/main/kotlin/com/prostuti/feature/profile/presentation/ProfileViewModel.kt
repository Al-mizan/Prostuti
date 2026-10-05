package com.prostuti.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prostuti.core.common.Result
import com.prostuti.core.common.UserStatsCalculator
import com.prostuti.feature.history.domain.GetUserAttemptsUseCase
import com.prostuti.feature.profile.domain.GetProfileUseCase
import com.prostuti.feature.profile.domain.ProfileRepository
import com.prostuti.feature.profile.domain.UpdateProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val getUserAttemptsUseCase: GetUserAttemptsUseCase,
    private val repository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            when (val result = getProfileUseCase()) {
                is Result.Success -> {
                    val attempts = when (val attemptsResult = getUserAttemptsUseCase()) {
                        is Result.Success -> attemptsResult.value
                        else -> emptyList()
                    }
                    val stats = UserStatsCalculator.calculate(attempts)
                    _uiState.value = ProfileUiState.Success(
                        profile = result.value,
                        stats = stats,
                    )
                }
                is Result.Error -> {
                    _uiState.value = ProfileUiState.Error(message = result.message)
                }
            }
        }
    }

    fun refreshStats() {
        val currentState = _uiState.value as? ProfileUiState.Success ?: return
        viewModelScope.launch {
            when (val attemptsResult = getUserAttemptsUseCase()) {
                is Result.Success -> {
                    val stats = UserStatsCalculator.calculate(attemptsResult.value)
                    _uiState.value = currentState.copy(stats = stats)
                }
                else -> Unit
            }
        }
    }

    fun updateName(newName: String) {
        val currentState = _uiState.value as? ProfileUiState.Success ?: return
        viewModelScope.launch {
            _uiState.value = currentState.copy(isUpdating = true, errorMessage = null)
            when (val result = updateProfileUseCase(name = newName, avatarId = currentState.profile.avatarId)) {
                is Result.Success -> {
                    _uiState.value = currentState.copy(
                        profile = result.value,
                        isUpdating = false,
                        errorMessage = null,
                    )
                }
                is Result.Error -> {
                    _uiState.value = currentState.copy(
                        isUpdating = false,
                        errorMessage = result.message,
                    )
                }
            }
        }
    }

    fun updateAvatar(newAvatarId: String) {
        val currentState = _uiState.value as? ProfileUiState.Success ?: return
        viewModelScope.launch {
            _uiState.value = currentState.copy(isUpdating = true, errorMessage = null)
            when (val result = updateProfileUseCase(name = currentState.profile.name, avatarId = newAvatarId)) {
                is Result.Success -> {
                    _uiState.value = currentState.copy(
                        profile = result.value,
                        isUpdating = false,
                        errorMessage = null,
                    )
                }
                is Result.Error -> {
                    _uiState.value = currentState.copy(
                        isUpdating = false,
                        errorMessage = result.message,
                    )
                }
            }
        }
    }

    fun logout() {
        repository.clearSession()
        _uiState.value = ProfileUiState.Loading
    }
}
