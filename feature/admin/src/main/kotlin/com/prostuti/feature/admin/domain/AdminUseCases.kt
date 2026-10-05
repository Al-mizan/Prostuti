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

class ImportCsvUseCase(private val repository: AdminRepository) {
    suspend operator fun invoke(
        type: QuestionType,
        fileName: String,
        bytes: ByteArray,
    ): Result<ImportSummary> = when (type) {
        QuestionType.BANK -> repository.importQuestionBankCsv(fileName, bytes)
        QuestionType.PRACTICE -> repository.importPracticeCsv(fileName, bytes)
    }
}

class GetAdminQuestionsUseCase(private val repository: AdminRepository) {
    suspend operator fun invoke(
        type: QuestionType? = null,
        examSession: String? = null,
        subject: Subject? = null,
        page: Int = 0,
        pageSize: Int = 50,
    ): Result<Page<AdminQuestionDto>> = repository.getQuestions(
        type = type,
        examSession = examSession,
        subject = subject,
        page = page,
        pageSize = pageSize,
    )
}

class UpdateAdminQuestionUseCase(private val repository: AdminRepository) {
    suspend operator fun invoke(
        id: String,
        request: UpdateQuestionRequest,
    ): Result<AdminQuestionDto> = repository.updateQuestion(id, request)
}

class DeleteAdminQuestionUseCase(private val repository: AdminRepository) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteQuestion(id)
}

class GetAdminUsersUseCase(private val repository: AdminRepository) {
    suspend operator fun invoke(): Result<List<AdminUserDto>> = repository.getUsers()
}

class UpdateUserRoleUseCase(private val repository: AdminRepository) {
    suspend operator fun invoke(
        userId: String,
        newRole: Role,
    ): Result<AdminUserDto> = repository.updateUserRole(userId, newRole)
}

class GetAdminModelTestsUseCase(private val repository: AdminRepository) {
    suspend operator fun invoke(): Result<List<com.prostuti.core.model.ModelTestDto>> = repository.getModelTests()
}

class CreateAdminModelTestUseCase(private val repository: AdminRepository) {
    suspend operator fun invoke(request: com.prostuti.core.model.CreateModelTestRequest): Result<com.prostuti.core.model.ModelTestDto> =
        repository.createModelTest(request)
}

class UpdateAdminModelTestUseCase(private val repository: AdminRepository) {
    suspend operator fun invoke(id: String, request: com.prostuti.core.model.UpdateModelTestRequest): Result<com.prostuti.core.model.ModelTestDto> =
        repository.updateModelTest(id, request)
}

class DeleteAdminModelTestUseCase(private val repository: AdminRepository) {
    suspend operator fun invoke(id: String): Result<Boolean> = repository.deleteModelTest(id)
}

