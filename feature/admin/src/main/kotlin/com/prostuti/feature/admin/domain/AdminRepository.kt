package com.prostuti.feature.admin.domain

import com.prostuti.core.common.Result
import com.prostuti.core.model.AdminQuestionDto
import com.prostuti.core.model.AdminUserDto
import com.prostuti.core.model.ImportSummary
import com.prostuti.core.model.Page
import com.prostuti.core.model.QuestionType
import com.prostuti.core.model.Role
import com.prostuti.core.model.Subject
import com.prostuti.core.model.UpdateQuestionRequest

/**
 * Domain repository contract for admin capabilities.
 */
interface AdminRepository {
    suspend fun importQuestionBankCsv(fileName: String, bytes: ByteArray): Result<ImportSummary>
    suspend fun importPracticeCsv(fileName: String, bytes: ByteArray): Result<ImportSummary>
    suspend fun getQuestions(
        type: QuestionType? = null,
        examSession: String? = null,
        subject: Subject? = null,
        page: Int = 0,
        pageSize: Int = 50,
    ): Result<Page<AdminQuestionDto>>
    suspend fun updateQuestion(id: String, request: UpdateQuestionRequest): Result<AdminQuestionDto>
    suspend fun deleteQuestion(id: String): Result<Unit>
    suspend fun getUsers(): Result<List<AdminUserDto>>
    suspend fun updateUserRole(id: String, role: Role): Result<AdminUserDto>
    suspend fun getModelTests(): Result<List<com.prostuti.core.model.ModelTestDto>>
    suspend fun createModelTest(request: com.prostuti.core.model.CreateModelTestRequest): Result<com.prostuti.core.model.ModelTestDto>
    suspend fun updateModelTest(id: String, request: com.prostuti.core.model.UpdateModelTestRequest): Result<com.prostuti.core.model.ModelTestDto>
    suspend fun deleteModelTest(id: String): Result<Boolean>
}
