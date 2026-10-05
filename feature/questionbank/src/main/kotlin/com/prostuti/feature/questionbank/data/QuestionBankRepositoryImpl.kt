package com.prostuti.feature.questionbank.data

import com.prostuti.core.common.Result
import com.prostuti.core.common.SessionStore
import com.prostuti.core.model.BcsSessionSummaryDto
import com.prostuti.core.model.ModelTestDto
import com.prostuti.core.model.Page
import com.prostuti.core.model.QuestionBankItemDto
import com.prostuti.core.model.Subject
import com.prostuti.core.network.ApiException
import com.prostuti.core.network.ModelTestApi
import com.prostuti.core.network.QuestionBankApi
import com.prostuti.feature.questionbank.domain.QuestionBankRepository
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.serialization.SerializationException

class QuestionBankRepositoryImpl(
    private val api: QuestionBankApi,
    private val modelTestApi: ModelTestApi,
    private val sessionStore: SessionStore,
) : QuestionBankRepository {

    override suspend fun getSessions(): Result<List<BcsSessionSummaryDto>> = try {
        val sessions = api.getSessions()
        Result.Success(sessions)
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

    override suspend fun getQuestions(
        session: String,
        subject: Subject?,
        page: Int,
        pageSize: Int,
    ): Result<Page<QuestionBankItemDto>> = try {
        val result = api.getQuestions(
            examSession = session,
            subject = subject,
            page = page,
            pageSize = pageSize,
        )
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

    override suspend fun getLiveModelTest(): Result<ModelTestDto?> = try {
        val result = modelTestApi.getLiveModelTest()
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

    override suspend fun getAllModelTests(status: String?): Result<List<ModelTestDto>> = try {
        val result = modelTestApi.getAllModelTests(status)
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
