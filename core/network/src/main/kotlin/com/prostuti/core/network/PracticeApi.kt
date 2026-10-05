package com.prostuti.core.network

import com.prostuti.core.model.ApiResponse
import com.prostuti.core.model.FinishPracticeSessionResponse
import com.prostuti.core.model.PracticeAnswerResultDto
import com.prostuti.core.model.PracticeSessionDto
import com.prostuti.core.model.StartPracticeSessionRequest
import com.prostuti.core.model.SubmitPracticeAnswerRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Network API client for subject-wise Practice sessions.
 */
class PracticeApi(private val client: HttpClient) {

    suspend fun startSession(request: StartPracticeSessionRequest): PracticeSessionDto {
        val response = client.post("api/v1/practice/sessions") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val envelope = response.body<ApiResponse<PracticeSessionDto>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }

    suspend fun submitAnswer(
        sessionId: String,
        request: SubmitPracticeAnswerRequest,
    ): PracticeAnswerResultDto {
        val response = client.post("api/v1/practice/sessions/$sessionId/answers") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val envelope = response.body<ApiResponse<PracticeAnswerResultDto>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }

    suspend fun finishSession(sessionId: String): FinishPracticeSessionResponse {
        val response = client.post("api/v1/practice/sessions/$sessionId/finish")
        val envelope = response.body<ApiResponse<FinishPracticeSessionResponse>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }
}
