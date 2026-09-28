package com.prostuti.core.model

import kotlinx.serialization.Serializable

/**
 * Wire format for the admin endpoints defined in docs/backend_design.md §6.
 * All DTOs live in commonMain so the Android client and the Ktor server
 * compile against the same shape (the only reason this module is KMP).
 *
 * Endpoint reference:
 *   GET    /api/v1/admin/questions           -> Page<AdminQuestionDto>
 *   PUT    /api/v1/admin/questions/{id}      <- UpdateQuestionRequest  -> AdminQuestionDto
 *   GET    /api/v1/admin/users               -> List<AdminUserDto>
 *   PUT    /api/v1/admin/users/{id}/role     <- UpdateRoleRequest      -> AdminUserDto
 *   POST   /api/v1/admin/{bank|practice}/import              -> ImportSummary
 */

@Serializable
data class AdminQuestionDto(
    val id: String,
    val type: QuestionType,
    val subject: Subject,
    val examSession: String? = null,
    val topic: String? = null,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: Option,
    val explanation: String? = null,
    val difficulty: Difficulty? = null,
    val createdAt: String,
)

/**
 * `type` is intentionally NOT editable — flipping BANK <-> PRACTICE would
 * either orphan examSession or require it on a practice row. Edit the row's
 * other fields; delete + re-import if the type is wrong.
 *
 * `examSession` is preserved as-is from the row (server enforces BANK-required).
 */
@Serializable
data class UpdateQuestionRequest(
    val subject: Subject,
    val topic: String? = null,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: Option,
    val explanation: String? = null,
    val difficulty: Difficulty? = null,
)

@Serializable
data class AdminUserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: Role,
    val createdAt: String,
)

@Serializable
data class UpdateRoleRequest(val role: Role)

@Serializable
data class CsvRowError(val row: Int, val message: String)

@Serializable
data class ImportSummary(val imported: Int, val rejected: List<CsvRowError>)

@Serializable
data class Page<T>(
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val total: Int,
)
