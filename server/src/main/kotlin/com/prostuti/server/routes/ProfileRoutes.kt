package com.prostuti.server.routes

import com.prostuti.core.model.UpdateProfileRequest
import com.prostuti.server.auth.UserPrincipal
import com.prostuti.server.services.ProfileService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.profileRoutes(profileService: ProfileService) {
    authenticate("auth-jwt") {
        route("/profile") {
            get {
                val principal = call.principal<UserPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val profile = profileService.getProfile(principal.userId)
                call.respond(HttpStatusCode.OK, profile)
            }

            put {
                val principal = call.principal<UserPrincipal>()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<UpdateProfileRequest>()
                val updated = profileService.updateProfile(principal.userId, request)
                call.respond(HttpStatusCode.OK, updated)
            }
        }
    }
}
