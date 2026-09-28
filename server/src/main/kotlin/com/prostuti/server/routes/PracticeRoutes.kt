package com.prostuti.server.routes

import com.prostuti.core.model.StartPracticeSessionRequest
import com.prostuti.core.model.SubmitPracticeAnswerRequest
import com.prostuti.server.auth.UserPrincipal
import com.prostuti.server.services.PracticeService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

fun Route.practiceRoutes(service: PracticeService) {
    authenticate("auth-jwt") {
        route("/practice") {

            // POST /api/v1/practice/sessions
            post("/sessions") {
                val userId = call.principal<UserPrincipal>()?.userId
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val req = call.receive<StartPracticeSessionRequest>()

                val session = service.startSession(
                    userId = userId,
                    subject = req.subject,
                    count = req.count,
                )
                call.respond(HttpStatusCode.Created, session)
            }

            // POST /api/v1/practice/sessions/{id}/answers
            post("/sessions/{id}/answers") {
                val userId = call.principal<UserPrincipal>()?.userId
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val sessionId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid session ID"))
                val req = call.receive<SubmitPracticeAnswerRequest>()
                val questionId = runCatching { UUID.fromString(req.questionId) }.getOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid question ID"))

                val result = service.submitAnswer(
                    userId = userId,
                    sessionId = sessionId,
                    questionId = questionId,
                    selectedOption = req.selectedOption,
                )
                call.respond(HttpStatusCode.OK, result)
            }

            // POST /api/v1/practice/sessions/{id}/finish
            post("/sessions/{id}/finish") {
                val userId = call.principal<UserPrincipal>()?.userId
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val sessionId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid session ID"))

                val summary = service.finishSession(
                    userId = userId,
                    sessionId = sessionId,
                )
                call.respond(HttpStatusCode.OK, summary)
            }
        }
    }
}
