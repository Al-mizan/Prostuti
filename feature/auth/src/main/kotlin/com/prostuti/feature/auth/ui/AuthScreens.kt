package com.prostuti.feature.auth.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.prostuti.core.designsystem.GoogleLogo
import com.prostuti.core.designsystem.ProstutiButton
import com.prostuti.core.designsystem.ProstutiLogo
import com.prostuti.core.designsystem.ProstutiTextField
import com.prostuti.feature.auth.presentation.AuthUiState
import com.prostuti.feature.auth.presentation.AuthViewModel
import kotlinx.coroutines.launch

/**
 * Shared screen body for Login and Register.
 * Enhanced with responsive scrolling, IME insets, and Google Credential Manager integration.
 */
@Composable
fun AuthScreen(
    mode: AuthUiState.Mode,
    viewModel: AuthViewModel,
    onAuthenticated: () -> Unit,
    onSwitchMode: () -> Unit,
    serverClientId: String = "",
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var googleAuthError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state) {
        if (state is AuthUiState.Authenticated) onAuthenticated()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProstutiLogo()

            Text(
                text = if (mode == AuthUiState.Mode.LOGIN) "লগইন করুন" else "নতুন অ্যাকাউন্ট তৈরি করুন",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp),
            )

            // Google Sign-In Button
            OutlinedButton(
                onClick = {
                    if (serverClientId.isBlank()) {
                        googleAuthError = "Google Client ID is not configured"
                        return@OutlinedButton
                    }
                    coroutineScope.launch {
                        try {
                            googleAuthError = null
                            val credentialManager = CredentialManager.create(context)
                            val googleIdOption = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(serverClientId)
                                .setAutoSelectEnabled(false)
                                .build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            val result = credentialManager.getCredential(
                                request = request,
                                context = context,
                            )
                            val credential = result.credential
                            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                viewModel.loginWithGoogle(googleIdTokenCredential.idToken)
                            }
                        } catch (e: GetCredentialCancellationException) {
                            // User cancelled flow; ignore
                        } catch (e: Exception) {
                            googleAuthError = e.message ?: "Google sign in failed"
                        }
                    }
                },
                enabled = (state as? AuthUiState.Idle)?.loading != true,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    GoogleLogo(modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Google দিয়ে চালিয়ে যান",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            // Divider: "অথবা" (or)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                )
                Text(
                    text = "অথবা",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 14.dp),
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                )
            }

            if (mode == AuthUiState.Mode.REGISTER) {
                ProstutiTextField(
                    value = (state as? AuthUiState.Idle)?.name ?: "",
                    onValueChange = viewModel::onNameChange,
                    label = "পূর্ণ নাম",
                )
                Spacer(Modifier.height(12.dp))
            }

            val idle = state as? AuthUiState.Idle
            ProstutiTextField(
                value = idle?.email ?: "",
                onValueChange = viewModel::onEmailChange,
                label = "ইমেইল",
            )
            Spacer(Modifier.height(12.dp))
            ProstutiTextField(
                value = idle?.password ?: "",
                onValueChange = viewModel::onPasswordChange,
                label = "পাসওয়ার্ড",
                isPassword = true,
            )

            val displayError = idle?.error ?: googleAuthError
            if (displayError != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = displayError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(Modifier.height(24.dp))

            ProstutiButton(
                text = if (mode == AuthUiState.Mode.LOGIN) "সাইন ইন করুন" else "অ্যাকাউন্ট তৈরি করুন",
                onClick = viewModel::submit,
                loading = idle?.loading == true,
            )

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = onSwitchMode) {
                Text(
                    text = if (mode == AuthUiState.Mode.LOGIN)
                        "নতুন ব্যবহারকারী? একটি অ্যাকাউন্ট তৈরি করুন"
                    else
                        "আগে থেকেই অ্যাকাউন্ট আছে? সাইন ইন করুন",
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}