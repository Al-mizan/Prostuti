package com.prostuti.feature.exam.presentation

import com.prostuti.core.model.Option

sealed interface ExamUiEvent {
    data class SelectSession(val sessionName: String) : ExamUiEvent
    data class SelectQuestionCount(val count: Int) : ExamUiEvent
    data class SelectDuration(val minutes: Int) : ExamUiEvent
    data object StartExam : ExamUiEvent

    data class SelectOption(val option: Option) : ExamUiEvent
    data object ClearOption : ExamUiEvent
    data object ToggleFlag : ExamUiEvent
    data class JumpToQuestion(val index: Int) : ExamUiEvent
    data object NextQuestion : ExamUiEvent
    data object PreviousQuestion : ExamUiEvent
    data object TogglePalette : ExamUiEvent
    data object ShowConfirmSubmitDialog : ExamUiEvent
    data object DismissConfirmSubmitDialog : ExamUiEvent
    data object ConfirmSubmit : ExamUiEvent

    data class SwitchResultTab(val tab: ResultTab) : ExamUiEvent
    data object RetakeExam : ExamUiEvent
    data object ResetToSetup : ExamUiEvent
}
