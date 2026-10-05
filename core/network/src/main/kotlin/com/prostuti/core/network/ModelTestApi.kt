package com.prostuti.core.network

import com.prostuti.core.model.ApiResponse
import com.prostuti.core.model.CreateModelTestRequest
import com.prostuti.core.model.ModelTestDto
import com.prostuti.core.model.UpdateModelTestRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonElement

interface ModelTestApi {
    suspend fun getLiveModelTest(): ModelTestDto?
    suspend fun getAllModelTests(status: String? = null): List<ModelTestDto>
    suspend fun createModelTest(request: CreateModelTestRequest): ModelTestDto
    suspend fun updateModelTest(id: String, request: UpdateModelTestRequest): ModelTestDto
    suspend fun deleteModelTest(id: String): Boolean
}

class ModelTestApiImpl(private val client: HttpClient) : ModelTestApi {

    override suspend fun getLiveModelTest(): ModelTestDto? {
        val response = client.get("api/v1/model-tests/live")
        val envelope = response.body<ApiResponse<ModelTestDto?>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data
    }

    override suspend fun getAllModelTests(status: String?): List<ModelTestDto> {
        val response = client.get("api/v1/model-tests") {
            if (!status.isNullOrBlank()) {
                parameter("status", status)
            }
        }
        val envelope = response.body<ApiResponse<List<ModelTestDto>>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: emptyList()
    }

    override suspend fun createModelTest(request: CreateModelTestRequest): ModelTestDto {
        val response = client.post("api/v1/admin/model-tests") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val envelope = response.body<ApiResponse<ModelTestDto>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }

    override suspend fun updateModelTest(id: String, request: UpdateModelTestRequest): ModelTestDto {
        val response = client.patch("api/v1/admin/model-tests/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val envelope = response.body<ApiResponse<ModelTestDto>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.data ?: throw ApiException(envelope.message, response.status.value)
    }

    override suspend fun deleteModelTest(id: String): Boolean {
        val response = client.delete("api/v1/admin/model-tests/$id")
        if (response.status == HttpStatusCode.NoContent) return true
        val envelope = response.body<ApiResponse<JsonElement?>>()
        if (!envelope.success) throw ApiException(envelope.message, response.status.value)
        return envelope.success
    }
}
