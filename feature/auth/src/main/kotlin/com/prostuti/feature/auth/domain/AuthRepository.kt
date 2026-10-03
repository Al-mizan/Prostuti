package com.prostuti.feature.auth.domain

import com.prostuti.core.common.Result
import com.prostuti.core.model.AuthResponse
import com.prostuti.core.model.MeResponse
import com.prostuti.core.model.Role

/**
 * Auth boundary the ViewModels depend on. One implementation in [data],
 * one interface here — no testability theater (SKILL.md §1).
 */
interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): Result<AuthResponse>
    suspend fun login(email: String, password: String): Result<AuthResponse>
    suspend fun loginWithGoogle(idToken: String): Result<AuthResponse>
    suspend fun me(): Result<MeResponse>
    fun clearSession()
    fun hasSession(): Boolean
    fun currentRole(): Role?
}
