package com.prostuti.server.routes

import com.prostuti.core.model.StartExamSessionRequest
import com.prostuti.core.model.SubmitExamRequest
import com.prostuti.server.auth.UserPrincipal
import com.prostuti.server.services.ExamService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

/**
 * Endpoints for solo timed BCS Exam Mode under /api/v1/exam and /api/v1/leaderboard.
 * All endpoints require JWT authentication.
 */
fun Route.examRoutes(service: ExamService) {
    authenticate("auth-jwt") {
        route("/exam") {
            // POST /api/v1/exam/sessions
            post("/sessions") {
                val principal = call.principal<UserPrincipal>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))

                val request = call.receive<StartExamSessionRequest>()
                val session = service.startSession(
                    userId = principal.userId,
                    examSession = request.examSession,
                    questionCount = request.questionCount,
                    durationMinutes = request.durationMinutes,
                )
                call.respond(HttpStatusCode.Created, session)
            }

            // POST /api/v1/exam/sessions/{id}/submit
            post("/sessions/{id}/submit") {
                val principal = call.principal<UserPrincipal>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))

                val sessionIdStr = call.parameters["id"]
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing session ID"))

                val sessionId = runCatching { UUID.fromString(sessionIdStr) }.getOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid session ID format"))

                val request = call.receive<SubmitExamRequest>()
                val result = service.submitExam(
                    userId = principal.userId,
                    sessionId = sessionId,
                    answers = request.answers,
                    timeTakenSeconds = request.timeTakenSeconds,
                )
                call.respond(HttpStatusCode.OK, result)
            }
        }

        // GET /api/v1/leaderboard/{examSession}
        get("/leaderboard/{examSession}") {
            val examSession = call.parameters["examSession"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Missing examSession parameter"))

            val leaderboard = service.getLeaderboard(examSession)
            call.respond(HttpStatusCode.OK, leaderboard)
        }
    }
}
