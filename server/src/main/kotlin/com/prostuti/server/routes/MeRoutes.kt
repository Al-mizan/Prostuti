package com.prostuti.server.routes

import com.prostuti.core.model.MeResponse
import com.prostuti.server.auth.AuthService
import com.prostuti.server.auth.UserPrincipal
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.meRoutes(authService: AuthService) {
    authenticate("auth-jwt") {
        get("/me") {
            val principal = call.principal<UserPrincipal>()
                ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val role = authService.roleFor(principal.userId) ?: principal.roleEnum
            call.respond(MeResponse(userId = principal.userId, role = role))
        }
    }
}
