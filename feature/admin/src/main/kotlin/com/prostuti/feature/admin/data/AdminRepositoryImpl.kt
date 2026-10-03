package com.prostuti.feature.admin.data

import com.prostuti.core.common.Result
import com.prostuti.core.common.SessionStore
import com.prostuti.core.model.AdminQuestionDto
import com.prostuti.core.model.AdminUserDto
import com.prostuti.core.model.ImportSummary
import com.prostuti.core.model.Page
import com.prostuti.core.model.QuestionType
import com.prostuti.core.model.Role
import com.prostuti.core.model.Subject
import com.prostuti.core.model.UpdateQuestionRequest
import com.prostuti.core.network.AdminApi
import com.prostuti.feature.admin.domain.AdminRepository
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerializationException

class AdminRepositoryImpl(
    private val api: AdminApi,
    private val sessionStore: SessionStore,
) : AdminRepository {

    override suspend fun importQuestionBankCsv(fileName: String, bytes: ByteArray): Result<ImportSummary> = try {
        val result = api.importQuestionBankCsv(fileName, bytes)
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

    override suspend fun importPracticeCsv(fileName: String, bytes: ByteArray): Result<ImportSummary> = try {
        val result = api.importPracticeCsv(fileName, bytes)
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

    override suspend fun getQuestions(
        type: QuestionType?,
        examSession: String?,
        subject: Subject?,
        page: Int,
        pageSize: Int,
    ): Result<Page<AdminQuestionDto>> = try {
        val result = api.getQuestions(
            type = type,
            examSession = examSession,
            subject = subject,
            page = page,
            pageSize = pageSize,
        )
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

    override suspend fun updateQuestion(id: String, request: UpdateQuestionRequest): Result<AdminQuestionDto> = try {
        val result = api.updateQuestion(id, request)
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

    override suspend fun deleteQuestion(id: String): Result<Unit> = try {
        api.deleteQuestion(id)
        Result.Success(Unit)
    } catch (e: ClientRequestException) {
        handleClientError(e)
    } catch (e: ServerResponseException) {
        Result.Error("Server error: ${e.response.status.value}")
    } catch (e: SerializationException) {
        Result.Error("Unexpected response format from server")
    } catch (e: Exception) {
        Result.Error(e.message ?: "Network error occurred")
    }

    override suspend fun getUsers(): Result<List<AdminUserDto>> = try {
        val result = api.getUsers()
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

    override suspend fun updateUserRole(id: String, role: Role): Result<AdminUserDto> = try {
        val result = api.updateUserRole(id, role)
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

    private suspend fun <T> handleClientError(e: ClientRequestException): Result<T> {
        if (e.response.status.value == 401) {
            sessionStore.clear()
            return Result.Error("Session expired. Please log in again.")
        }
        val body = runCatching { e.response.bodyAsText() }.getOrNull()
        val message = if (!body.isNullOrBlank()) {
            val match = Regex("\"error\"\\s*:\\s*\"([^\"]+)\"").find(body)
            match?.groupValues?.get(1) ?: body
        } else {
            "Request failed (${e.response.status.value})"
        }
        return Result.Error(message)
    }
}
