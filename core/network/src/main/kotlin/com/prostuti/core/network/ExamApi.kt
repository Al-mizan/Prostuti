package com.prostuti.core.network

import com.prostuti.core.model.ApiResponse
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

    suspend fun startSession(request: StartExamSessionRequest): ExamSessionDto {
        val response = client.post("api/v1/exam/sessions") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val envelope = response.body<ApiResponse<ExamSessionDto>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }

    suspend fun submitExam(
        sessionId: String,
        request: SubmitExamRequest,
    ): ExamResultDto {
        val response = client.post("api/v1/exam/sessions/$sessionId/submit") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val envelope = response.body<ApiResponse<ExamResultDto>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }

    suspend fun getLeaderboard(examSession: String): List<LeaderboardEntryDto> {
        val response = client.get("api/v1/leaderboard/$examSession")
        val envelope = response.body<ApiResponse<List<LeaderboardEntryDto>>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }
}
