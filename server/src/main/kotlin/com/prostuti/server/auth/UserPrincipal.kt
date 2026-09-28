package com.prostuti.server.auth

import com.prostuti.core.model.Role
import io.ktor.server.auth.*

/**
 * What the JWT principal looks like after validation. Call
 * `call.principal<UserPrincipal>()` to read.
 */
data class UserPrincipal(
    val userId: String,
    val role: String,
) : Principal {
    val roleEnum: Role
        get() = runCatching { Role.valueOf(role) }.getOrDefault(Role.STUDENT)
}
