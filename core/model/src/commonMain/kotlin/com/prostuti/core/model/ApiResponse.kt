package com.prostuti.core.model

import kotlinx.serialization.Serializable

/**
 * Standard API response envelope shared across all endpoints.
 * Conforms to the TypeScript backend contract { success, message, data, meta }.
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val meta: MetaDto? = null,
)

/**
 * Standard pagination metadata returned in envelopes for paginated endpoints.
 */
@Serializable
data class MetaDto(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int,
)
