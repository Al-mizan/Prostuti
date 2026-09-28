package com.prostuti.feature.practice.presentation

import com.prostuti.core.model.FinishPracticeSessionResponse
import com.prostuti.core.model.Option
import com.prostuti.core.model.PracticeAnswerResultDto
import com.prostuti.core.model.PracticeSessionQuestionDto
import com.prostuti.core.model.Subject

sealed interface PracticeUiState {
    data class SubjectSelect(
        val selectedCount: Int = 10,
    ) : PracticeUiState

    data class Loading(val message: String = "লোড হচ্ছে...") : PracticeUiState

    data class ActiveSession(
        val sessionId: String,
        val subject: Subject,
        val questions: List<PracticeSessionQuestionDto>,
        val currentIndex: Int = 0,
        val answers: Map<String, PracticeAnswerResultDto> = emptyMap(),
        val isSubmittingAnswer: Boolean = false,
        val isFinishing: Boolean = false,
    ) : PracticeUiState {
        val currentQuestion: PracticeSessionQuestionDto get() = questions[currentIndex]
        val totalQuestions: Int get() = questions.size
        val hasNextQuestion: Boolean get() = currentIndex < questions.size - 1
        val currentAnswerResult: PracticeAnswerResultDto? get() = answers[currentQuestion.id]
        val isCurrentQuestionAnswered: Boolean get() = currentAnswerResult != null
    }

    data class SessionSummary(
        val summary: FinishPracticeSessionResponse,
        val questions: List<PracticeSessionQuestionDto>,
        val answers: Map<String, PracticeAnswerResultDto>,
    ) : PracticeUiState

    data class Error(val message: String) : PracticeUiState
}

sealed interface PracticeUiEvent {
    data class ChangeQuestionCount(val count: Int) : PracticeUiEvent
    data class StartSession(val subject: Subject, val count: Int = 10) : PracticeUiEvent
    data class SubmitAnswer(val option: Option) : PracticeUiEvent
    data object NextQuestion : PracticeUiEvent
    data object FinishSession : PracticeUiEvent
    data object ResetToSubjectSelect : PracticeUiEvent
}
