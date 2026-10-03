package com.prostuti.server.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import com.prostuti.core.model.AuthResponse
import com.prostuti.core.model.Role
import com.prostuti.server.db.DatabaseFactory.dbQuery
import com.prostuti.server.db.tables.UsersTable
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.util.UUID

/**
 * Authentication use cases. Routes call into this; this never touches
 * HTTP. Errors are thrown as IllegalArgumentException (400) or
 * IllegalStateException with a clear message; StatusPages will translate
 * them to JSON.
 */
class AuthService {

    /** Minimum length is the only validation we enforce. See SKILL.md §1 (no ceremony). */
    private fun validatePassword(password: String) {
        require(password.length >= MIN_PASSWORD_LENGTH) {
            "Password must be at least $MIN_PASSWORD_LENGTH characters"
        }
        require(password.length <= MAX_PASSWORD_LENGTH) {
            "Password must be at most $MAX_PASSWORD_LENGTH characters"
        }
    }

    private fun validateEmail(email: String) {
        require(email.contains("@") && email.length in 3..254) { "Invalid email" }
    }

    private fun validateName(name: String) {
        require(name.isNotBlank()) { "Name must not be blank" }
    }

    private fun hashPassword(password: String): String =
        BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray())

    private fun verifyPassword(password: String, hash: String): Boolean =
        BCrypt.verifyer().verify(password.toCharArray(), hash).verified

    suspend fun register(name: String, email: String, password: String): AuthResponse {
        validateName(name)
        validateEmail(email.trim().lowercase())
        validatePassword(password)

        val normalizedEmail = email.trim().lowercase()

        return dbQuery {
            val existing = UsersTable
                .selectAll()
                .where { UsersTable.email eq normalizedEmail }
                .limit(1)
                .firstOrNull()
            require(existing == null) { "Email already registered" }

            val userId = UUID.randomUUID()
            UsersTable.insert {
                it[id] = userId
                it[UsersTable.name] = name.trim()
                it[UsersTable.email] = normalizedEmail
                it[passwordHash] = hashPassword(password)
                it[role] = Role.STUDENT
                it[avatarId] = "mascot_1"
                it[createdAt] = Clock.System.now()
            }
            AuthResponse(
                token = JwtConfig.makeToken(userId.toString(), Role.STUDENT.name),
                userId = userId.toString(),
                role = Role.STUDENT,
            )
        }
    }

    suspend fun login(email: String, password: String): AuthResponse {
        validateEmail(email.trim().lowercase())
        val normalizedEmail = email.trim().lowercase()

        return dbQuery {
            val row = UsersTable
                .selectAll()
                .where { UsersTable.email eq normalizedEmail }
                .limit(1)
                .firstOrNull()
                ?: throw IllegalArgumentException("Invalid credentials")

            val hash = row[UsersTable.passwordHash]
                ?: throw IllegalArgumentException("This account uses Google Sign-In. Please sign in with Google.")
            require(verifyPassword(password, hash)) { "Invalid credentials" }

            val userId = row[UsersTable.id].toString()
            val role = row[UsersTable.role]
            AuthResponse(
                token = JwtConfig.makeToken(userId, role.name),
                userId = userId,
                role = role,
            )
        }
    }

    suspend fun loginWithGoogle(
        idToken: String,
        verifier: GoogleTokenVerifier = GoogleAuthVerifier()
    ): AuthResponse {
        val googleUser = verifier.verify(idToken)
            ?: throw IllegalArgumentException("Invalid Google token")

        val normalizedEmail = googleUser.email.trim().lowercase()

        return dbQuery {
            val existing = UsersTable
                .selectAll()
                .where { (UsersTable.googleId eq googleUser.googleId) or (UsersTable.email eq normalizedEmail) }
                .limit(1)
                .firstOrNull()

            if (existing != null) {
                val userId = existing[UsersTable.id].toString()
                val role = existing[UsersTable.role]

                if (existing[UsersTable.googleId] == null) {
                    UsersTable.update({ UsersTable.id eq existing[UsersTable.id] }) {
                        it[googleId] = googleUser.googleId
                    }
                }

                AuthResponse(
                    token = JwtConfig.makeToken(userId, role.name),
                    userId = userId,
                    role = role,
                )
            } else {
                val userId = UUID.randomUUID()
                UsersTable.insert {
                    it[id] = userId
                    it[name] = googleUser.name
                    it[email] = normalizedEmail
                    it[passwordHash] = null
                    it[googleId] = googleUser.googleId
                    it[role] = Role.STUDENT
                    it[avatarId] = "mascot_1"
                    it[createdAt] = Clock.System.now()
                }

                AuthResponse(
                    token = JwtConfig.makeToken(userId.toString(), Role.STUDENT.name),
                    userId = userId.toString(),
                    role = Role.STUDENT,
                )
            }
        }
    }

    /**
     * First-admin bootstrap. Guarded by `BOOTSTRAP_TOKEN` env var at the
     * route layer, not here. Idempotent: if any admin already exists, this
     * is a no-op (returns that admin's token).
     *
     * Uses a simple admin name + email + password — the dev team is expected
     * to share these out-of-band.
     */
    suspend fun bootstrapAdmin(name: String, email: String, password: String): AuthResponse {
        validateName(name)
        validateEmail(email.trim().lowercase())
        validatePassword(password)
        val normalizedEmail = email.trim().lowercase()

        return dbQuery {
            val existingAdmin = UsersTable
                .selectAll()
                .where { UsersTable.role eq Role.ADMIN }
                .limit(1)
                .firstOrNull()
            if (existingAdmin != null) {
                val userId = existingAdmin[UsersTable.id].toString()
                val role = existingAdmin[UsersTable.role]
                return@dbQuery AuthResponse(
                    token = JwtConfig.makeToken(userId, role.name),
                    userId = userId,
                    role = role,
                )
            }
            val userId = UUID.randomUUID()
            UsersTable.insert {
                it[id] = userId
                it[UsersTable.name] = name.trim()
                it[UsersTable.email] = normalizedEmail
                it[passwordHash] = hashPassword(password)
                it[role] = Role.ADMIN
                it[avatarId] = "mascot_1"
                it[createdAt] = Clock.System.now()
            }
            AuthResponse(
                token = JwtConfig.makeToken(userId.toString(), Role.ADMIN.name),
                userId = userId.toString(),
                role = Role.ADMIN,
            )
        }
    }

    /** Used by GET /me. */
    suspend fun roleFor(userId: String): Role? = dbQuery {
        UsersTable
            .selectAll()
            .where { UsersTable.id eq UUID.fromString(userId) }
            .limit(1)
            .firstOrNull()?.get(UsersTable.role)
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 8
        const val MAX_PASSWORD_LENGTH = 128
        const val BCRYPT_COST = 10
    }
}