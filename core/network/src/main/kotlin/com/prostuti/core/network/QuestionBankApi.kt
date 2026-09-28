package com.prostuti.core.network

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

    suspend fun getSessions(): List<BcsSessionSummaryDto> =
        client.get("api/v1/question-bank/sessions").body()

    suspend fun getQuestions(
        examSession: String,
        subject: Subject? = null,
        page: Int = 0,
        pageSize: Int = 20,
    ): Page<QuestionBankItemDto> = client.get("api/v1/question-bank") {
        parameter("examSession", examSession)
        if (subject != null) {
            parameter("subject", subject.name)
        }
        parameter("page", page)
        parameter("pageSize", pageSize)
    }.body()
}
