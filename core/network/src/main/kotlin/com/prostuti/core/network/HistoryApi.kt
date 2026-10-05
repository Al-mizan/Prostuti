package com.prostuti.core.network

import com.prostuti.core.model.ApiResponse
import com.prostuti.core.model.Subject
import com.prostuti.core.model.UserAttemptSummaryDto
import com.prostuti.core.model.WrongAnswerItemDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * Network API client for user attempts history and derived wrong answers.
 */
class HistoryApi(private val client: HttpClient) {

    suspend fun getAttempts(): List<UserAttemptSummaryDto> {
        val response = client.get("api/v1/history/attempts")
        val envelope = response.body<ApiResponse<List<UserAttemptSummaryDto>>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }

    suspend fun getWrongAnswers(subject: Subject? = null): List<WrongAnswerItemDto> {
        val response = client.get("api/v1/history/wrong-answers") {
            if (subject != null) {
                parameter("subject", subject.name)
            }
        }
        val envelope = response.body<ApiResponse<List<WrongAnswerItemDto>>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }
}
