package com.prostuti.server.routes

import com.prostuti.core.model.BootstrapAdminRequest
import com.prostuti.core.model.LoginRequest
import com.prostuti.core.model.RegisterRequest
import com.prostuti.server.auth.AuthService
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            val req = call.receive<RegisterRequest>()
            val res = authService.register(req.name, req.email, req.password)
            call.respond(HttpStatusCode.Created, res)
        }
        post("/login") {
            val req = call.receive<LoginRequest>()
            val res = authService.login(req.email, req.password)
            call.respond(HttpStatusCode.OK, res)
        }
        post("/bootstrap-admin") {
            // Constant-time compare against BOOTSTRAP_TOKEN env var.
            // Bootstrap is one-shot: the route only exists while no admin is
            // registered, but the dev-time guard is the shared token.
            val token = call.request.headers["X-Bootstrap-Token"]
            val expected = resolveBootstrapToken()
            if (expected == null || token == null || !constantTimeEquals(token, expected)) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid bootstrap token"))
                return@post
            }
            val req = call.receive<BootstrapAdminRequest>()
            val res = authService.bootstrapAdmin(req.name, req.email, req.password)
            call.respond(HttpStatusCode.OK, res)
        }
    }
}

private fun resolveBootstrapToken(): String? {
    val dotenv = dotenv { ignoreIfMissing = true; directory = "./" }
    val dotenvParent = dotenv { ignoreIfMissing = true; directory = "../" }
    return dotenv["BOOTSTRAP_TOKEN"]
        ?: dotenvParent["BOOTSTRAP_TOKEN"]
        ?: System.getenv("BOOTSTRAP_TOKEN")
}

private fun constantTimeEquals(a: String, b: String): Boolean {
    if (a.length != b.length) return false
    var result = 0
    for (i in a.indices) result = result or (a[i].code xor b[i].code)
    return result == 0
}
