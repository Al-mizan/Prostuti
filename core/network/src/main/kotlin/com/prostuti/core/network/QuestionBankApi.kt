package com.prostuti.core.network

import com.prostuti.core.model.ApiResponse
import com.prostuti.core.model.BcsSessionSummaryDto
import com.prostuti.core.model.Page
import com.prostuti.core.model.QuestionBankItemDto
import com.prostuti.core.model.Subject
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * Network API client for student Question Bank exploration.
 */
class QuestionBankApi(private val client: HttpClient) {

    suspend fun getSessions(): List<BcsSessionSummaryDto> {
        val response = client.get("api/v1/question-bank/sessions")
        val envelope = response.body<ApiResponse<List<BcsSessionSummaryDto>>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }

    suspend fun getQuestions(
        examSession: String,
        subject: Subject? = null,
        page: Int = 0,
        pageSize: Int = 20,
    ): Page<QuestionBankItemDto> {
        val response = client.get("api/v1/question-bank") {
            parameter("examSession", examSession)
            if (subject != null) {
                parameter("subject", subject.name)
            }
            parameter("page", page)
            parameter("pageSize", pageSize)
        }
        val envelope = response.body<ApiResponse<Page<QuestionBankItemDto>>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }
}
