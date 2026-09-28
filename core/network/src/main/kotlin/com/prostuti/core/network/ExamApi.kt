package com.prostuti.core.network

import com.prostuti.core.model.ExamResultDto
import com.prostuti.core.model.ExamSessionDto
import com.prostuti.core.model.LeaderboardEntryDto
import com.prostuti.core.model.StartExamSessionRequest
import com.prostuti.core.model.SubmitExamRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Network API client for solo timed BCS Exam Mode & Leaderboard.
 */
class ExamApi(private val client: HttpClient) {

    suspend fun startSession(request: StartExamSessionRequest): ExamSessionDto =
        client.post("api/v1/exam/sessions") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun submitExam(
        sessionId: String,
        request: SubmitExamRequest,
    ): ExamResultDto =
        client.post("api/v1/exam/sessions/$sessionId/submit") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun getLeaderboard(examSession: String): List<LeaderboardEntryDto> =
        client.get("api/v1/leaderboard/$examSession").body()
}
