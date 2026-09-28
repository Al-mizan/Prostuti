package com.prostuti.feature.history.data

import com.prostuti.core.common.Result
import com.prostuti.core.common.SessionStore
import com.prostuti.core.model.Subject
import com.prostuti.core.model.UserAttemptSummaryDto
import com.prostuti.core.model.WrongAnswerItemDto
import com.prostuti.core.network.HistoryApi
import com.prostuti.feature.history.domain.HistoryRepository
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.serialization.SerializationException

class HistoryRepositoryImpl(
    private val api: HistoryApi,
    private val sessionStore: SessionStore,
) : HistoryRepository {

    override suspend fun getAttempts(): Result<List<UserAttemptSummaryDto>> = try {
        val result = api.getAttempts()
        Result.Success(result)
    } catch (e: ClientRequestException) {
        handleClientError(e)
    } catch (e: ServerResponseException) {
        Result.Error("Server error: ${e.response.status.value}")
    } catch (e: SerializationException) {
        Result.Error("Unexpected response format from server")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Network error occurred")
    }

    override suspend fun getWrongAnswers(subject: Subject?): Result<List<WrongAnswerItemDto>> = try {
        val result = api.getWrongAnswers(subject)
        Result.Success(result)
    } catch (e: ClientRequestException) {
        handleClientError(e)
    } catch (e: ServerResponseException) {
        Result.Error("Server error: ${e.response.status.value}")
    } catch (e: SerializationException) {
        Result.Error("Unexpected response format from server")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Network error occurred")
    }

    private fun <T> handleClientError(e: ClientRequestException): Result<T> {
        return if (e.response.status.value == 401) {
            sessionStore.clear()
            Result.Error("Session expired. Please log in again.")
        } else {
            Result.Error("Request failed (${e.response.status.value})")
        }
    }
}
