package com.prostuti.core.model

import kotlinx.serialization.Serializable

/**
 * Wire format for both client and server. Lives in commonMain so both sides
 * compile against the same shape — no separate DTOs to drift.
 *
 * See docs/backend_design.md §8 (Auth & role-based access).
 */

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class BootstrapAdminRequest(
    val name: String,
    val email: String,
    val password: String,
)

/**
 * Returned by /login, /register, and /bootstrap-admin. The token is a JWT
 * carrying `sub` (userId) and `role` claims.
 */
@Serializable
data class AuthResponse(
    val token: String,
    val userId: String,
    val role: Role,
)

/**
 * Returned by GET /me — proves the bearer token round-trips and that the
 * role claim is distinguishable server-side.
 */
@Serializable
data class MeResponse(
    val userId: String,
    val role: Role,
)
