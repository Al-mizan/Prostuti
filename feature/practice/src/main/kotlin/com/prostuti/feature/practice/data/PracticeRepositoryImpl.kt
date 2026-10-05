package com.prostuti.feature.practice.data

import com.prostuti.core.common.Result
import com.prostuti.core.common.SessionStore
import com.prostuti.core.model.FinishPracticeSessionResponse
import com.prostuti.core.model.Option
import com.prostuti.core.model.PracticeAnswerResultDto
import com.prostuti.core.model.PracticeSessionDto
import com.prostuti.core.model.StartPracticeSessionRequest
import com.prostuti.core.model.Subject
import com.prostuti.core.model.SubmitPracticeAnswerRequest
import com.prostuti.core.network.ApiException
import com.prostuti.core.network.PracticeApi
import com.prostuti.feature.practice.domain.PracticeRepository
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.serialization.SerializationException

class PracticeRepositoryImpl(
    private val api: PracticeApi,
    private val sessionStore: SessionStore,
) : PracticeRepository {

    override suspend fun startSession(subject: Subject, count: Int): Result<PracticeSessionDto> = try {
        val result = api.startSession(StartPracticeSessionRequest(subject, count))
        Result.Success(result)
    } catch (e: ApiException) {
        handleApiException(e)
    } catch (e: ClientRequestException) {
        handleClientError(e)
    } catch (e: ServerResponseException) {
        Result.Error("Server error: ${e.response.status.value}")
    } catch (e: SerializationException) {
        Result.Error("Unexpected response format from server")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Network error occurred")
    }

    override suspend fun submitAnswer(
        sessionId: String,
        questionId: String,
        selectedOption: Option,
    ): Result<PracticeAnswerResultDto> = try {
        val result = api.submitAnswer(sessionId, SubmitPracticeAnswerRequest(questionId, selectedOption))
        Result.Success(result)
    } catch (e: ApiException) {
        handleApiException(e)
    } catch (e: ClientRequestException) {
        handleClientError(e)
    } catch (e: ServerResponseException) {
        Result.Error("Server error: ${e.response.status.value}")
    } catch (e: SerializationException) {
        Result.Error("Unexpected response format from server")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Network error occurred")
    }

    override suspend fun finishSession(sessionId: String): Result<FinishPracticeSessionResponse> = try {
        val result = api.finishSession(sessionId)
        Result.Success(result)
    } catch (e: ApiException) {
        handleApiException(e)
    } catch (e: ClientRequestException) {
        handleClientError(e)
    } catch (e: ServerResponseException) {
        Result.Error("Server error: ${e.response.status.value}")
    } catch (e: SerializationException) {
        Result.Error("Unexpected response format from server")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Network error occurred")
    }

    private fun <T> handleApiException(e: ApiException): Result<T> {
        return if (e.statusCode == 401 || e.statusCode == 403) {
            sessionStore.clear()
            Result.Error("Session expired. Please log in again.", cause = e)
        } else {
            Result.Error(e.message, cause = e)
        }
    }

    private fun <T> handleClientError(e: ClientRequestException): Result<T> {
        return if (e.response.status.value == 401) {
            sessionStore.clear()
            Result.Error("Session expired. Please log in again.")
        } else {
            Result.Error("Failed request (${e.response.status.value})")
        }
    }
}
