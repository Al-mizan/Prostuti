package com.prostuti.feature.questionbank.presentation

import com.prostuti.core.model.BcsSessionSummaryDto
import com.prostuti.core.model.ModelTestDto
import com.prostuti.core.model.ModelTestStatus
import com.prostuti.core.model.Option
import com.prostuti.core.model.QuestionBankItemDto
import com.prostuti.core.model.Subject
import kotlin.math.ceil

enum class QuestionBankView {
    HOME,
    BCS_SESSIONS,
    STUDY,
    MODEL_TESTS,
}

sealed interface QuestionBankUiState {
    data object Loading : QuestionBankUiState
    data class Error(val message: String) : QuestionBankUiState
    data class Success(
        val view: QuestionBankView = QuestionBankView.HOME,
        val sessions: List<BcsSessionSummaryDto>,
        val selectedSession: String,
        val selectedSubject: Subject? = null,
        val questions: List<QuestionBankItemDto>,
        val page: Int,
        val pageSize: Int = 20,
        val totalQuestions: Int,
        val selectedOptions: Map<String, Option> = emptyMap(),
        val expandedExplanations: Set<String> = emptySet(),
        val isRefreshingQuestions: Boolean = false,
        val activeModalSession: BcsSessionSummaryDto? = null,
        val searchQuery: String = "",
        val liveModelTest: ModelTestDto? = null,
        val modelTests: List<ModelTestDto> = emptyList(),
        val isLoadingModelTests: Boolean = false,
        val selectedModelTestFilter: ModelTestStatus? = null,
    ) : QuestionBankUiState {
        val totalPages: Int
            get() = if (totalQuestions == 0) 1 else ceil(totalQuestions.toDouble() / pageSize).toInt()
        val hasNextPage: Boolean get() = page < totalPages - 1
        val hasPreviousPage: Boolean get() = page > 0

        val filteredSessions: List<BcsSessionSummaryDto>
            get() = if (searchQuery.isBlank()) {
                sessions
            } else {
                sessions.filter { it.sessionName.contains(searchQuery, ignoreCase = true) }
            }

        val filteredModelTests: List<ModelTestDto>
            get() {
                var list = modelTests
                if (selectedModelTestFilter != null) {
                    list = list.filter { it.status == selectedModelTestFilter }
                }
                if (searchQuery.isNotBlank()) {
                    list = list.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                            it.examSession.contains(searchQuery, ignoreCase = true) ||
                            (it.description?.contains(searchQuery, ignoreCase = true) == true)
                    }
                }
                return list
            }
    }
}

sealed interface QuestionBankUiEvent {
    data class NavigateView(val view: QuestionBankView) : QuestionBankUiEvent
    data class OpenSubjectStudy(val subject: Subject) : QuestionBankUiEvent
    data class OpenSessionModal(val session: BcsSessionSummaryDto) : QuestionBankUiEvent
    data object CloseSessionModal : QuestionBankUiEvent
    data class UpdateSearchQuery(val query: String) : QuestionBankUiEvent
    data class SelectSession(val sessionName: String) : QuestionBankUiEvent
    data class SelectSubject(val subject: Subject?) : QuestionBankUiEvent
    data class SelectOption(val questionId: String, val option: Option) : QuestionBankUiEvent
    data class ToggleExplanation(val questionId: String) : QuestionBankUiEvent
    data class ChangePage(val page: Int) : QuestionBankUiEvent
    data object Retry : QuestionBankUiEvent
    data class FilterModelTests(val status: ModelTestStatus?) : QuestionBankUiEvent
    data object RefreshModelTests : QuestionBankUiEvent
}
