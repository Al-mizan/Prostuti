package com.prostuti.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prostuti.core.common.Result
import com.prostuti.feature.auth.domain.LoginUseCase
import com.prostuti.feature.auth.domain.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * One ViewModel per screen, per SKILL.md §3 — but Login and Register share
 * the same shape, so they share this ViewModel via a `mode` parameter
 * passed into the factory. Keeps state machinery in one place.
 */
class AuthViewModel(
    private val mode: AuthUiState.Mode,
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle(mode))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(v: String) = _uiState.update { st ->
        if (st is AuthUiState.Idle) st.copy(email = v, error = null) else st
    }
    fun onPasswordChange(v: String) = _uiState.update { st ->
        if (st is AuthUiState.Idle) st.copy(password = v, error = null) else st
    }
    fun onNameChange(v: String) = _uiState.update { st ->
        if (st is AuthUiState.Idle) st.copy(name = v, error = null) else st
    }

    fun submit() {
        val st = _uiState.value
        if (st !is AuthUiState.Idle || st.loading) return

        // Client-side guard so we don't ping the server for obvious cases.
        when (mode) {
            AuthUiState.Mode.LOGIN -> {
                if (st.email.isBlank() || st.password.isBlank()) {
                    _uiState.update { (it as AuthUiState.Idle).copy(error = "Email and password required") }
                    return
                }
            }
            AuthUiState.Mode.REGISTER -> {
                if (st.name.isBlank() || st.email.isBlank() || st.password.length < 8) {
                    _uiState.update {
                        (it as AuthUiState.Idle).copy(error = "All fields required; password ≥ 8 chars")
                    }
                    return
                }
            }
        }

        viewModelScope.launch {
            _uiState.update { (it as AuthUiState.Idle).copy(loading = true, error = null) }
            val result = when (mode) {
                AuthUiState.Mode.LOGIN -> loginUseCase(st.email, st.password)
                AuthUiState.Mode.REGISTER -> registerUseCase(st.name, st.email, st.password)
            }
            _uiState.update {
                when (result) {
                    is Result.Success ->
                        AuthUiState.Authenticated(mode, result.value)
                    is Result.Error ->
                        (it as AuthUiState.Idle).copy(loading = false, error = result.message)
                }
            }
        }
    }
}
