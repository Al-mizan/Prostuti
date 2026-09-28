package com.prostuti.server

import com.prostuti.server.auth.AuthService
import com.prostuti.server.auth.installJwt
import com.prostuti.server.db.DatabaseFactory
import com.prostuti.server.routes.apiRoutes
import com.prostuti.server.services.AdminService
import com.prostuti.server.services.ProfileService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 5000) {
        DatabaseFactory.init()
        installPlugins()
        installJwt()

        val authService = AuthService()
        val adminService = AdminService()
        val profileService = ProfileService()
        val questionBankService = com.prostuti.server.services.QuestionBankService()
        val practiceService = com.prostuti.server.services.PracticeService()
        val examService = com.prostuti.server.services.ExamService()
        routing {
            get("/health") {
                call.respondText("OK", ContentType.Text.Plain, HttpStatusCode.OK)
            }
        }
        apiRoutes(authService, adminService, profileService, questionBankService, practiceService, examService)
    }.start(wait = true)
}

private fun Application.installPlugins() {
    install(CallLogging) {
        level = Level.INFO
    }
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader("X-Bootstrap-Token")
        anyHost() // For local development only
    }
    install(ContentNegotiation) {
        json()
    }
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to (cause.message ?: "Bad request")))
        }
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to (cause.message ?: "Bad request")))
        }
        exception<IllegalStateException> { call, cause ->
            call.respond(HttpStatusCode.Conflict, mapOf("error" to (cause.message ?: "Conflict")))
        }
        exception<NoSuchElementException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, mapOf("error" to (cause.message ?: "Not found")))
        }
        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled error", cause)
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal server error"))
        }
    }
}
