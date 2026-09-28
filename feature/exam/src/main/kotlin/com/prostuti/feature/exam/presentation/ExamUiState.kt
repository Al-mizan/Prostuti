package com.prostuti.feature.exam.presentation

import com.prostuti.core.model.BcsSessionSummaryDto
import com.prostuti.core.model.ExamQuestionDto
import com.prostuti.core.model.ExamResultDto
import com.prostuti.core.model.LeaderboardEntryDto
import com.prostuti.core.model.Option

enum class ResultTab {
    ANALYSIS,
    LEADERBOARD,
}

sealed interface ExamUiState {

    data class Setup(
        val availableSessions: List<BcsSessionSummaryDto> = emptyList(),
        val selectedSession: String = "",
        val selectedQuestionCount: Int = 50,
        val selectedDurationMinutes: Int = 30,
        val isLoadingSessions: Boolean = true,
        val errorMessage: String? = null,
    ) : ExamUiState

    data class Loading(val message: String) : ExamUiState

    data class ActiveExam(
        val sessionId: String,
        val examSession: String,
        val questions: List<ExamQuestionDto>,
        val currentIndex: Int = 0,
        val answers: Map<String, Option> = emptyMap(),
        val flaggedQuestions: Set<String> = emptySet(),
        val remainingSeconds: Int,
        val initialDurationSeconds: Int,
        val isPaletteVisible: Boolean = false,
        val isConfirmSubmitDialogOpen: Boolean = false,
        val isSubmitting: Boolean = false,
    ) : ExamUiState {
        val currentQuestion: ExamQuestionDto get() = questions[currentIndex]
        val totalQuestions: Int get() = questions.size
        val answeredCount: Int get() = answers.size
        val unansweredCount: Int get() = totalQuestions - answeredCount
        val flaggedCount: Int get() = flaggedQuestions.size
        val hasNext: Boolean get() = currentIndex < questions.size - 1
        val hasPrevious: Boolean get() = currentIndex > 0
    }

    data class ResultSummary(
        val result: ExamResultDto,
        val activeTab: ResultTab = ResultTab.ANALYSIS,
        val leaderboard: List<LeaderboardEntryDto> = emptyList(),
        val isLoadingLeaderboard: Boolean = false,
    ) : ExamUiState

    data class Error(val message: String) : ExamUiState
}
