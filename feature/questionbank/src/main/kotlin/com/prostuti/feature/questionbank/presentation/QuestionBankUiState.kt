package com.prostuti.feature.questionbank.presentation

import com.prostuti.core.model.BcsSessionSummaryDto
import com.prostuti.core.model.Option
import com.prostuti.core.model.QuestionBankItemDto
import com.prostuti.core.model.Subject
import kotlin.math.ceil

sealed interface QuestionBankUiState {
    data object Loading : QuestionBankUiState
    data class Error(val message: String) : QuestionBankUiState
    data class Success(
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
    ) : QuestionBankUiState {
        val totalPages: Int
            get() = if (totalQuestions == 0) 1 else ceil(totalQuestions.toDouble() / pageSize).toInt()
        val hasNextPage: Boolean get() = page < totalPages - 1
        val hasPreviousPage: Boolean get() = page > 0
    }
}

sealed interface QuestionBankUiEvent {
    data class SelectSession(val sessionName: String) : QuestionBankUiEvent
    data class SelectSubject(val subject: Subject?) : QuestionBankUiEvent
    data class SelectOption(val questionId: String, val option: Option) : QuestionBankUiEvent
    data class ToggleExplanation(val questionId: String) : QuestionBankUiEvent
    data class ChangePage(val page: Int) : QuestionBankUiEvent
    data object Retry : QuestionBankUiEvent
}
