package com.prostuti.feature.admin.presentation

import com.prostuti.core.model.AdminQuestionDto
import com.prostuti.core.model.CreateModelTestRequest
import com.prostuti.core.model.ModelTestDto
import com.prostuti.core.model.QuestionType
import com.prostuti.core.model.Role
import com.prostuti.core.model.Subject
import com.prostuti.core.model.UpdateModelTestRequest
import com.prostuti.core.model.UpdateQuestionRequest

sealed interface AdminUiEvent {
    data class SelectTab(val tab: AdminTab) : AdminUiEvent

    // CSV Import Events
    data class SelectTargetQuestionType(val type: QuestionType) : AdminUiEvent
    data class SelectCsvFile(val fileName: String, val bytes: ByteArray) : AdminUiEvent
    data object ClearSelectedCsvFile : AdminUiEvent
    data object TriggerCsvImport : AdminUiEvent
    data object DismissImportResult : AdminUiEvent

    // Questions Tab Events
    data class SetFilterType(val type: QuestionType?) : AdminUiEvent
    data class SetFilterSubject(val subject: Subject?) : AdminUiEvent
    data class SetFilterExamSession(val examSession: String?) : AdminUiEvent
    data class ChangeQuestionsPage(val page: Int) : AdminUiEvent
    data class OpenEditQuestionDialog(val question: AdminQuestionDto) : AdminUiEvent
    data object DismissEditQuestionDialog : AdminUiEvent
    data class SubmitEditQuestion(val id: String, val request: UpdateQuestionRequest) : AdminUiEvent
    data class RequestDeleteQuestion(val question: AdminQuestionDto) : AdminUiEvent
    data object DismissDeleteQuestionDialog : AdminUiEvent
    data object ConfirmDeleteQuestion : AdminUiEvent
    data object RefreshQuestions : AdminUiEvent

    // Users Tab Events
    data object RefreshUsers : AdminUiEvent
    data class ToggleUserRole(val userId: String, val currentRole: Role) : AdminUiEvent

    // Model Tests Tab Events
    data object RefreshModelTests : AdminUiEvent
    data object OpenCreateModelTestDialog : AdminUiEvent
    data class OpenEditModelTestDialog(val modelTest: ModelTestDto) : AdminUiEvent
    data object DismissModelTestDialog : AdminUiEvent
    data class CreateModelTest(val request: CreateModelTestRequest) : AdminUiEvent
    data class UpdateModelTest(val id: String, val request: UpdateModelTestRequest) : AdminUiEvent
    data class RequestDeleteModelTest(val modelTest: ModelTestDto) : AdminUiEvent
    data object DismissDeleteModelTestDialog : AdminUiEvent
    data object ConfirmDeleteModelTest : AdminUiEvent

    // Notifications
    data object DismissUserMessage : AdminUiEvent
}
