package com.prostuti.core.network

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

    suspend fun getAttempts(): List<UserAttemptSummaryDto> =
        client.get("api/v1/history/attempts").body()

    suspend fun getWrongAnswers(subject: Subject? = null): List<WrongAnswerItemDto> =
        client.get("api/v1/history/wrong-answers") {
            if (subject != null) {
                parameter("subject", subject.name)
            }
        }.body()
}
