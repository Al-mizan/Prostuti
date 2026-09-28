package com.prostuti.server.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.Date

/**
 * Fails fast on missing JWT_SECRET — matches the DatabaseFactory behavior
 * for DB_URL. The first query will not be the time to discover the secret
 * is empty.
 *
 * Accepts env, then `.env` at repo root, then `.env` one level above
 * (mirrors DatabaseFactory).
 */
object JwtConfig {
    private fun loadSecret(): String {
        val dotenv = dotenv { ignoreIfMissing = true; directory = "./" }
        val dotenvParent = dotenv { ignoreIfMissing = true; directory = "../" }
        return dotenv["JWT_SECRET"]
            ?: dotenvParent["JWT_SECRET"]
            ?: System.getenv("JWT_SECRET")
            ?: error("JWT_SECRET is not set. Add it to .env or your environment.")
    }

    private val secret: String by lazy { loadSecret() }
    private const val ISSUER = "prostuti"
    private const val VALIDITY_SECONDS = 60L * 60L * 24L * 7L // 7 days

    val algorithm: Algorithm by lazy { Algorithm.HMAC256(secret) }

    val verifier: com.auth0.jwt.JWTVerifier by lazy {
        JWT.require(algorithm).withIssuer(ISSUER).build()
    }

    fun makeToken(userId: String, role: String): String =
        JWT.create()
            .withSubject(userId)
            .withClaim("role", role)
            .withIssuer(ISSUER)
            .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_SECONDS * 1000))
            .sign(algorithm)
}

/**
 * Top-level extension that wires the JWT principal into Ktor's auth pipeline.
 * Top-level (not a member of JwtConfig) so it can be called as `installJwt()`
 * inside the embeddedServer block without a dispatch receiver.
 */
fun Application.installJwt() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "prostuti"
            verifier(JwtConfig.verifier)
            validate { credential ->
                val userId = credential.payload.subject
                val role = credential.payload.getClaim("role").asString()
                if (userId != null && role != null) {
                    UserPrincipal(userId, role)
                } else null
            }
        }
    }
}
