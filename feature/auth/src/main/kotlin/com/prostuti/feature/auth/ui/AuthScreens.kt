package com.prostuti.feature.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prostuti.core.designsystem.ProstutiButton
import com.prostuti.core.designsystem.ProstutiLogo
import com.prostuti.core.designsystem.ProstutiTextField
import com.prostuti.feature.auth.presentation.AuthUiState
import com.prostuti.feature.auth.presentation.AuthViewModel

/**
 * Shared screen body. Both Login and Register pass their mode in via
 * the ViewModel; the UI is identical except for which fields are shown
 * and which "alternate" link is offered.
 */
@Composable
fun AuthScreen(
    mode: AuthUiState.Mode,
    viewModel: AuthViewModel,
    onAuthenticated: () -> Unit,
    onSwitchMode: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        if (state is AuthUiState.Authenticated) onAuthenticated()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProstutiLogo()

            Text(
                text = if (mode == AuthUiState.Mode.LOGIN) "Sign in" else "Create your account",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            if (mode == AuthUiState.Mode.REGISTER) {
                ProstutiTextField(
                    value = (state as? AuthUiState.Idle)?.name ?: "",
                    onValueChange = viewModel::onNameChange,
                    label = "Full name",
                )
                Spacer(Modifier.height(12.dp))
            }

            val idle = state as? AuthUiState.Idle
            ProstutiTextField(
                value = idle?.email ?: "",
                onValueChange = viewModel::onEmailChange,
                label = "Email",
            )
            Spacer(Modifier.height(12.dp))
            ProstutiTextField(
                value = idle?.password ?: "",
                onValueChange = viewModel::onPasswordChange,
                label = "Password",
                isPassword = true,
            )

            idle?.error?.let { msg ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(Modifier.height(24.dp))

            ProstutiButton(
                text = if (mode == AuthUiState.Mode.LOGIN) "Sign in" else "Create account",
                onClick = viewModel::submit,
                loading = idle?.loading == true,
            )

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = onSwitchMode) {
                Text(
                    text = if (mode == AuthUiState.Mode.LOGIN)
                        "New here? Create an account"
                    else
                        "Already have an account? Sign in",
                )
            }
        }
    }
}