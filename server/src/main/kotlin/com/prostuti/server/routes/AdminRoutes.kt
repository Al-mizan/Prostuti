package com.prostuti.server.routes

import com.prostuti.core.model.ImportSummary
import com.prostuti.core.model.QuestionType
import com.prostuti.core.model.Subject
import com.prostuti.core.model.UpdateQuestionRequest
import com.prostuti.core.model.UpdateRoleRequest
import com.prostuti.server.auth.UserPrincipal
import com.prostuti.server.csv.CsvImporter
import com.prostuti.server.services.AdminService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID

/**
 * All /api/v1/admin/ routes. Mounted via the `adminOnly { … }` helper
 * from Routing.kt (SKILL.md §6) so role gating happens in one place.
 *
 * Routes:
 *   POST   /admin/question-bank/import
 *   POST   /admin/practice-questions/import
 *   GET    /admin/questions                       (type?, examSession?, subject?, page, pageSize)
 *   PUT    /admin/questions/{id}
 *   DELETE /admin/questions/{id}
 *   GET    /admin/users
 *   PUT    /admin/users/{id}/role
 */
fun Route.adminRoutes(adminService: AdminService) {
    adminOnly {
        route("/admin") {

            // ── CSV import ────────────────────────────────────────────
            post("/question-bank/import") {
                val userId = call.principal<UserPrincipal>()?.userId
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)
                importCsv(call, type = QuestionType.BANK, userId = userId, service = adminService)
            }

            post("/practice-questions/import") {
                val userId = call.principal<UserPrincipal>()?.userId
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)
                importCsv(call, type = QuestionType.PRACTICE, userId = userId, service = adminService)
            }

            // ── Question list / edit / delete ─────────────────────────
            get("/questions") {
                val type = call.request.queryParameters["type"]?.let {
                    runCatching { QuestionType.valueOf(it) }.getOrNull()
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "type must be BANK or PRACTICE"),
                        )
                }
                val examSession = call.request.queryParameters["examSession"]
                val subject = call.request.queryParameters["subject"]?.let {
                    runCatching { Subject.valueOf(it) }.getOrNull()
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "subject is not a valid value"),
                        )
                }
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull() ?: 50

                val result = adminService.listQuestions(
                    type = type,
                    examSession = examSession,
                    subject = subject,
                    page = page,
                    pageSize = pageSize,
                )
                call.respond(result)
            }

            put("/questions/{id}") {
                val id = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: return@put call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "id must be a UUID"),
                    )
                val req = call.receive<UpdateQuestionRequest>()
                try {
                    val updated = adminService.updateQuestion(id, req)
                    call.respond(HttpStatusCode.OK, updated)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to (e.message ?: "not found")))
                } catch (e: IllegalStateException) {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to (e.message ?: "conflict")))
                }
            }

            delete("/questions/{id}") {
                val id = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "id must be a UUID"),
                    )
                try {
                    adminService.deleteQuestion(id)
                    call.respond(HttpStatusCode.NoContent)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to (e.message ?: "not found")))
                } catch (e: IllegalStateException) {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to (e.message ?: "conflict")))
                }
            }

            // ── User management ───────────────────────────────────────
            get("/users") {
                val users = adminService.listUsers()
                call.respond(users)
            }

            put("/users/{id}/role") {
                val id = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: return@put call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "id must be a UUID"),
                    )
                val req = call.receive<UpdateRoleRequest>()
                try {
                    val updated = adminService.updateUserRole(id, req.role)
                    call.respond(HttpStatusCode.OK, updated)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to (e.message ?: "not found")))
                } catch (e: IllegalStateException) {
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to (e.message ?: "conflict")))
                }
            }
        }
    }
}

private suspend fun importCsv(
    call: ApplicationCall,
    type: QuestionType,
    userId: String,
    service: AdminService,
) {
    val multipart = call.receiveMultipart(formFieldLimit = MAX_FILE_BYTES)
    var fileBytesRead = false

    multipart.forEachPart { part ->
        if (part is PartData.FileItem && part.name == "file") {
            fileBytesRead = true
            val bytes = part.streamProvider().use { it.readAllBytes() }
            if (bytes.size > MAX_FILE_BYTES) {
                call.respond(
                    HttpStatusCode.PayloadTooLarge,
                    mapOf("error" to "CSV exceeds $MAX_FILE_BYTES bytes"),
                )
                return@forEachPart
            }
            val reader = BufferedReader(InputStreamReader(bytes.inputStream(), Charsets.UTF_8))
            val outcome = when (type) {
                QuestionType.BANK -> CsvImporter.parseQuestionBank(reader)
                QuestionType.PRACTICE -> CsvImporter.parsePractice(reader)
            }
            when (outcome) {
                is CsvImporter.ParseOutcome.Failed -> {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to outcome.message))
                }
                is CsvImporter.ParseOutcome.Ok -> {
                    val summary: ImportSummary = when (type) {
                        QuestionType.BANK -> service.importQuestionBank(
                            userId = userId,
                            rows = outcome.parsed.filterIsInstance<CsvImporter.ParsedRow.Bank>(),
                        ).copy(rejected = outcome.rejected)
                        QuestionType.PRACTICE -> service.importPractice(
                            userId = userId,
                            rows = outcome.parsed.filterIsInstance<CsvImporter.ParsedRow.Practice>(),
                        ).copy(rejected = outcome.rejected)
                    }
                    call.respond(HttpStatusCode.OK, summary)
                }
            }
        }
        part.dispose()
    }

    if (!fileBytesRead) {
        call.respond(
            HttpStatusCode.BadRequest,
            mapOf("error" to "missing 'file' form field"),
        )
    }
}

private const val MAX_FILE_BYTES: Long = 5L * 1024 * 1024  // 5 MB
