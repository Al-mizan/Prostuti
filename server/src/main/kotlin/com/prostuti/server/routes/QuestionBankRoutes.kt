package com.prostuti.server.routes

import com.prostuti.core.model.Subject
import com.prostuti.server.services.QuestionBankService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Student Question Bank routes under /api/v1/question-bank.
 * All routes require authentication.
 */
fun Route.questionBankRoutes(service: QuestionBankService) {
    authenticate("auth-jwt") {
        route("/question-bank") {

            // GET /api/v1/question-bank/sessions
            get("/sessions") {
                val sessions = service.listSessions()
                call.respond(sessions)
            }

            // GET /api/v1/question-bank?examSession=...&subject=...&page=0&pageSize=20
            get {
                val sessions = service.listSessions()
                val requestedSession = call.request.queryParameters["examSession"]
                val sessionName = when {
                    !requestedSession.isNullOrBlank() -> requestedSession
                    sessions.isNotEmpty() -> sessions.first().sessionName
                    else -> ""
                }

                if (sessionName.isBlank()) {
                    call.respond(
                        HttpStatusCode.OK,
                        mapOf("items" to emptyList<Any>(), "page" to 0, "pageSize" to 0, "total" to 0)
                    )
                    return@get
                }

                val subject = call.request.queryParameters["subject"]?.let {
                    runCatching { Subject.valueOf(it) }.getOrNull()
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "Invalid subject: $it")
                        )
                }

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val pageSize = (call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 20).coerceIn(1, 200)

                val result = service.listQuestions(
                    examSession = sessionName,
                    subject = subject,
                    page = page,
                    pageSize = pageSize,
                )
                call.respond(HttpStatusCode.OK, result)
            }
        }
    }
}
