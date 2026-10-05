package com.prostuti.core.network

import com.prostuti.core.model.AdminQuestionDto
import com.prostuti.core.model.AdminUserDto
import com.prostuti.core.model.ApiResponse
import com.prostuti.core.model.ImportSummary
import com.prostuti.core.model.Page
import com.prostuti.core.model.QuestionType
import com.prostuti.core.model.Role
import com.prostuti.core.model.Subject
import com.prostuti.core.model.UpdateQuestionRequest
import com.prostuti.core.model.UpdateRoleRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonElement

/**
 * Network API client for role-gated admin operations:
 * - Multipart CSV import (Question Bank & Practice Questions)
 * - Question review & CRUD
 * - User listing & role management
 */
class AdminApi(private val client: HttpClient) {

    suspend fun importQuestionBankCsv(fileName: String, bytes: ByteArray): ImportSummary {
        val response = client.submitFormWithBinaryData(
            url = "api/v1/admin/question-bank/import",
            formData = formData {
                append("file", bytes, Headers.build {
                    append(HttpHeaders.ContentType, "text/csv")
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            }
        )
        val envelope = response.body<ApiResponse<ImportSummary>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }

    suspend fun importPracticeCsv(fileName: String, bytes: ByteArray): ImportSummary {
        val response = client.submitFormWithBinaryData(
            url = "api/v1/admin/practice-questions/import",
            formData = formData {
                append("file", bytes, Headers.build {
                    append(HttpHeaders.ContentType, "text/csv")
                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                })
            }
        )
        val envelope = response.body<ApiResponse<ImportSummary>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }

    suspend fun getQuestions(
        type: QuestionType? = null,
        examSession: String? = null,
        subject: Subject? = null,
        page: Int = 0,
        pageSize: Int = 50,
    ): Page<AdminQuestionDto> {
        val response = client.get("api/v1/admin/questions") {
            parameter("page", page)
            parameter("pageSize", pageSize)
            if (type != null) parameter("type", type.name)
            if (!examSession.isNullOrBlank()) parameter("examSession", examSession)
            if (subject != null) parameter("subject", subject.name)
        }
        val envelope = response.body<ApiResponse<Page<AdminQuestionDto>>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }

    suspend fun updateQuestion(id: String, request: UpdateQuestionRequest): AdminQuestionDto {
        val response = client.put("api/v1/admin/questions/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val envelope = response.body<ApiResponse<AdminQuestionDto>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }

    suspend fun deleteQuestion(id: String) {
        val response = client.delete("api/v1/admin/questions/$id")
        if (response.status == HttpStatusCode.NoContent) return
        val envelope = response.body<ApiResponse<JsonElement?>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
    }

    suspend fun getUsers(): List<AdminUserDto> {
        val response = client.get("api/v1/admin/users")
        val envelope = response.body<ApiResponse<List<AdminUserDto>>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }

    suspend fun updateUserRole(id: String, role: Role): AdminUserDto {
        val response = client.put("api/v1/admin/users/$id/role") {
            contentType(ContentType.Application.Json)
            setBody(UpdateRoleRequest(role))
        }
        val envelope = response.body<ApiResponse<AdminUserDto>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }
}
