package com.prostuti.server.routes

import com.prostuti.core.model.Role
import com.prostuti.server.auth.AuthService
import com.prostuti.server.auth.UserPrincipal
import com.prostuti.server.services.AdminService
import com.prostuti.server.services.ProfileService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Mounts all /api/v1 routes. Future features add their route files here.
 */
fun Application.apiRoutes(
    authService: AuthService,
    adminService: AdminService,
    profileService: ProfileService,
    questionBankService: com.prostuti.server.services.QuestionBankService,
) {
    routing {
        route("/api/v1") {
            authRoutes(authService)
            meRoutes(authService)
            profileRoutes(profileService)
            adminRoutes(adminService)
            questionBankRoutes(questionBankService)
            // practiceRoutes(...), examRoutes(...), etc. land here in later segments.
        }
    }
}

/**
 * SKILL.md §6 adminOnly pattern. Future admin route files use:
 *
 *     adminOnly {
 *         get("/admin/questions") { ... }
 *     }
 *
 * Role gating happens here, server-side, always — client-side hiding of
 * admin screens is UX only.
 *
 * Implementation note: Ktor's `authenticate("name") { ... }` runs the JWT
 * validation during the `Plugins` phase. Reading `call.principal` in a
 * `Plugins`-phase intercept therefore sees `null` (principal is set after
 * the interceptor is registered). We therefore gate on the `Call` phase,
 * which Ktor runs AFTER `Plugins`, by which time the principal is populated.
 */
fun Route.adminOnly(build: Route.() -> Unit) = authenticate("auth-jwt") {
    intercept(ApplicationCallPipeline.Call) {
        val role = call.principal<UserPrincipal>()?.roleEnum
        if (role != Role.ADMIN) {
            call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Admin access required"))
            finish()
        }
    }
    build()
}
