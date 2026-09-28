package com.prostuti.feature.history.presentation

import com.prostuti.core.model.Subject

sealed interface HistoryUiEvent {
    data class SwitchTab(val tab: HistoryTab) : HistoryUiEvent
    data class FilterAttemptType(val filter: AttemptTypeFilter) : HistoryUiEvent
    data class FilterWrongAnswerSubject(val subject: Subject?) : HistoryUiEvent
    data object Refresh : HistoryUiEvent
}
