package com.prostuti.feature.auth.domain

import com.prostuti.core.common.Result
import com.prostuti.core.model.AuthResponse
import com.prostuti.core.model.MeResponse

class LoginUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<AuthResponse> =
        repo.login(email.trim(), password)
}

class RegisterUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<AuthResponse> {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        if (trimmedName.length < 2) return Result.Error("Please enter your name")
        if (!trimmedEmail.contains("@")) return Result.Error("Please enter a valid email")
        if (password.length < 8) return Result.Error("Password must be at least 8 characters")
        return repo.register(trimmedName, trimmedEmail, password)
    }
}

class GetCurrentUserUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(): Result<MeResponse> = repo.me()
}

class LoginWithGoogleUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(idToken: String): Result<AuthResponse> =
        repo.loginWithGoogle(idToken)
}
