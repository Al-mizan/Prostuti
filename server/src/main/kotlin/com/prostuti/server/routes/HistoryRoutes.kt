package com.prostuti.server.routes

import com.prostuti.core.model.Subject
import com.prostuti.server.auth.UserPrincipal
import com.prostuti.server.services.HistoryService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Endpoints for user history and derived wrong answers under /api/v1/history.
 * All endpoints require JWT authentication.
 */
fun Route.historyRoutes(service: HistoryService) {
    authenticate("auth-jwt") {
        route("/history") {
            // GET /api/v1/history/attempts
            get("/attempts") {
                val principal = call.principal<UserPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))

                val attempts = service.getAttempts(principal.userId)
                call.respond(HttpStatusCode.OK, attempts)
            }

            // GET /api/v1/history/wrong-answers?subject=...
            get("/wrong-answers") {
                val principal = call.principal<UserPrincipal>()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))

                val subjectParam = call.request.queryParameters["subject"]
                val subject = subjectParam?.let {
                    runCatching { Subject.valueOf(it) }.getOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid subject: $it"))
                }

                val wrongAnswers = service.getWrongAnswers(principal.userId, subject)
                call.respond(HttpStatusCode.OK, wrongAnswers)
            }
        }
    }
}
