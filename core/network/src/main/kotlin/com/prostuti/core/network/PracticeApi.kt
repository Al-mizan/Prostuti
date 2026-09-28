package com.prostuti.core.network

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

    suspend fun startSession(request: StartPracticeSessionRequest): PracticeSessionDto =
        client.post("api/v1/practice/sessions") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun submitAnswer(
        sessionId: String,
        request: SubmitPracticeAnswerRequest,
    ): PracticeAnswerResultDto =
        client.post("api/v1/practice/sessions/$sessionId/answers") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun finishSession(sessionId: String): FinishPracticeSessionResponse =
        client.post("api/v1/practice/sessions/$sessionId/finish").body()
}
