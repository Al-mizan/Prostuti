package com.prostuti.feature.auth.presentation

import com.prostuti.core.model.AuthResponse

/**
 * Single UiState shape shared by Login and Register. The screen's
 * `Mode` is set when the ViewModel is constructed and never mutates,
 * so it's captured at the type level rather than as state.
 */
sealed interface AuthUiState {

    enum class Mode { LOGIN, REGISTER }

    /**
     * Editing state: user is filling the form, may have already submitted
     * once and seen an error. `loading=true` means a request is in flight.
     */
    data class Idle(
        val mode: Mode,
        val name: String = "",
        val email: String = "",
        val password: String = "",
        val loading: Boolean = false,
        val error: String? = null,
    ) : AuthUiState

    /**
     * Authenticated: terminal state for the screen. The Compose layer
     * observes this via LaunchedEffect and calls `onAuthenticated()`.
     */
    data class Authenticated(
        val mode: Mode,
        val response: AuthResponse,
    ) : AuthUiState
}
