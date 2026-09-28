package com.prostuti.feature.history.presentation

import com.prostuti.core.model.SessionType
import com.prostuti.core.model.Subject
import com.prostuti.core.model.UserAttemptSummaryDto
import com.prostuti.core.model.WrongAnswerItemDto

enum class HistoryTab {
    ATTEMPTS,
    WRONG_ANSWERS,
}

enum class AttemptTypeFilter {
    ALL,
    PRACTICE,
    EXAM,
}

sealed interface HistoryUiState {
    data object Loading : HistoryUiState

    data class Content(
        val activeTab: HistoryTab = HistoryTab.ATTEMPTS,
        val allAttempts: List<UserAttemptSummaryDto> = emptyList(),
        val selectedAttemptTypeFilter: AttemptTypeFilter = AttemptTypeFilter.ALL,
        val allWrongAnswers: List<WrongAnswerItemDto> = emptyList(),
        val selectedSubjectFilter: Subject? = null,
        val isLoadingWrongAnswers: Boolean = false,
        val errorMessage: String? = null,
    ) : HistoryUiState {
        val filteredAttempts: List<UserAttemptSummaryDto>
            get() = when (selectedAttemptTypeFilter) {
                AttemptTypeFilter.ALL -> allAttempts
                AttemptTypeFilter.PRACTICE -> allAttempts.filter { it.sessionType == SessionType.PRACTICE }
                AttemptTypeFilter.EXAM -> allAttempts.filter { it.sessionType == SessionType.EXAM }
            }

        val filteredWrongAnswers: List<WrongAnswerItemDto>
            get() = if (selectedSubjectFilter == null) {
                allWrongAnswers
            } else {
                allWrongAnswers.filter { it.subject == selectedSubjectFilter }
            }
    }

    data class Error(val message: String) : HistoryUiState
}
