package com.prostuti.feature.exam.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prostuti.core.common.Result
import com.prostuti.core.model.ExamAnswerSubmission
import com.prostuti.core.model.Option
import com.prostuti.feature.exam.domain.GetAvailableSessionsUseCase
import com.prostuti.feature.exam.domain.GetLeaderboardUseCase
import com.prostuti.feature.exam.domain.StartExamSessionUseCase
import com.prostuti.feature.exam.domain.SubmitExamUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ExamViewModel(
    private val getAvailableSessions: GetAvailableSessionsUseCase,
    private val startExamSession: StartExamSessionUseCase,
    private val submitExamUseCase: SubmitExamUseCase,
    private val getLeaderboardUseCase: GetLeaderboardUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExamUiState>(ExamUiState.Setup())
    val uiState: StateFlow<ExamUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadAvailableSessions()
    }

    fun onEvent(event: ExamUiEvent) {
        when (event) {
            is ExamUiEvent.SelectSession -> {
                val current = _uiState.value as? ExamUiState.Setup ?: return
                _uiState.value = current.copy(selectedSession = event.sessionName)
            }
            is ExamUiEvent.SelectQuestionCount -> {
                val current = _uiState.value as? ExamUiState.Setup ?: return
                _uiState.value = current.copy(selectedQuestionCount = event.count)
            }
            is ExamUiEvent.SelectDuration -> {
                val current = _uiState.value as? ExamUiState.Setup ?: return
                _uiState.value = current.copy(selectedDurationMinutes = event.minutes)
            }
            is ExamUiEvent.StartExam -> startExam()

            is ExamUiEvent.SelectOption -> selectOption(event.option)
            is ExamUiEvent.ClearOption -> clearOption()
            is ExamUiEvent.ToggleFlag -> toggleFlag()
            is ExamUiEvent.JumpToQuestion -> jumpToQuestion(event.index)
            is ExamUiEvent.NextQuestion -> nextQuestion()
            is ExamUiEvent.PreviousQuestion -> previousQuestion()
            is ExamUiEvent.TogglePalette -> togglePalette()
            is ExamUiEvent.ShowConfirmSubmitDialog -> showSubmitDialog(true)
            is ExamUiEvent.DismissConfirmSubmitDialog -> showSubmitDialog(false)
            is ExamUiEvent.ConfirmSubmit -> submitExam()

            is ExamUiEvent.SwitchResultTab -> switchResultTab(event.tab)
            is ExamUiEvent.RetakeExam -> startExam()
            is ExamUiEvent.ResetToSetup -> resetToSetup()
        }
    }

    private fun loadAvailableSessions() {
        viewModelScope.launch {
            when (val result = getAvailableSessions()) {
                is Result.Success -> {
                    val sessions = result.value
                    val defaultSession = sessions.firstOrNull()?.sessionName ?: "47th BCS Preliminary"
                    _uiState.value = ExamUiState.Setup(
                        availableSessions = sessions,
                        selectedSession = defaultSession,
                        isLoadingSessions = false,
                    )
                }
                is Result.Error -> {
                    _uiState.value = ExamUiState.Setup(
                        isLoadingSessions = false,
                        errorMessage = result.message,
                    )
                }
            }
        }
    }

    private fun startExam() {
        val setup = _uiState.value as? ExamUiState.Setup
        val sessionName = setup?.selectedSession ?: "47th BCS Preliminary"
        val count = setup?.selectedQuestionCount ?: 50
        val durationMins = setup?.selectedDurationMinutes ?: 30

        _uiState.value = ExamUiState.Loading("পরীক্ষার প্রশ্নপত্র প্রস্তুত করা হচ্ছে...")

        viewModelScope.launch {
            when (val result = startExamSession(sessionName, count, durationMins)) {
                is Result.Error -> {
                    _uiState.value = ExamUiState.Error(result.message)
                }
                is Result.Success -> {
                    val session = result.value
                    val totalSecs = session.durationMinutes * 60

                    _uiState.value = ExamUiState.ActiveExam(
                        sessionId = session.id,
                        examSession = session.examSession,
                        questions = session.questions,
                        currentIndex = 0,
                        remainingSeconds = totalSecs,
                        initialDurationSeconds = totalSecs,
                    )

                    startTimer()
                }
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val current = _uiState.value as? ExamUiState.ActiveExam ?: break
                if (current.remainingSeconds <= 1) {
                    _uiState.value = current.copy(remainingSeconds = 0)
                    submitExam()
                    break
                } else {
                    _uiState.value = current.copy(remainingSeconds = current.remainingSeconds - 1)
                }
            }
        }
    }

    private fun selectOption(option: Option) {
        val current = _uiState.value as? ExamUiState.ActiveExam ?: return
        if (current.isSubmitting) return

        val qId = current.currentQuestion.id
        _uiState.value = current.copy(
            answers = current.answers + (qId to option)
        )
    }

    private fun clearOption() {
        val current = _uiState.value as? ExamUiState.ActiveExam ?: return
        if (current.isSubmitting) return

        val qId = current.currentQuestion.id
        _uiState.value = current.copy(
            answers = current.answers - qId
        )
    }

    private fun toggleFlag() {
        val current = _uiState.value as? ExamUiState.ActiveExam ?: return
        val qId = current.currentQuestion.id
        val flags = current.flaggedQuestions
        val updated = if (flags.contains(qId)) flags - qId else flags + qId
        _uiState.value = current.copy(flaggedQuestions = updated)
    }

    private fun jumpToQuestion(index: Int) {
        val current = _uiState.value as? ExamUiState.ActiveExam ?: return
        if (index in 0 until current.totalQuestions) {
            _uiState.value = current.copy(
                currentIndex = index,
                isPaletteVisible = false,
            )
        }
    }

    private fun nextQuestion() {
        val current = _uiState.value as? ExamUiState.ActiveExam ?: return
        if (current.hasNext) {
            _uiState.value = current.copy(currentIndex = current.currentIndex + 1)
        }
    }

    private fun previousQuestion() {
        val current = _uiState.value as? ExamUiState.ActiveExam ?: return
        if (current.hasPrevious) {
            _uiState.value = current.copy(currentIndex = current.currentIndex - 1)
        }
    }

    private fun togglePalette() {
        val current = _uiState.value as? ExamUiState.ActiveExam ?: return
        _uiState.value = current.copy(isPaletteVisible = !current.isPaletteVisible)
    }

    private fun showSubmitDialog(show: Boolean) {
        val current = _uiState.value as? ExamUiState.ActiveExam ?: return
        _uiState.value = current.copy(isConfirmSubmitDialogOpen = show)
    }

    private fun submitExam() {
        timerJob?.cancel()
        val current = _uiState.value as? ExamUiState.ActiveExam ?: return
        if (current.isSubmitting) return

        val timeTakenSeconds = (current.initialDurationSeconds - current.remainingSeconds).coerceAtLeast(1)
        val answerSubmissions = current.answers.map { (qId, option) ->
            ExamAnswerSubmission(questionId = qId, selectedOption = option)
        }

        _uiState.value = current.copy(isSubmitting = true, isConfirmSubmitDialogOpen = false)

        viewModelScope.launch {
            when (val result = submitExamUseCase(current.sessionId, answerSubmissions, timeTakenSeconds)) {
                is Result.Error -> {
                    _uiState.value = ExamUiState.Error(result.message)
                }
                is Result.Success -> {
                    val examResult = result.value
                    _uiState.value = ExamUiState.ResultSummary(
                        result = examResult,
                        activeTab = ResultTab.ANALYSIS,
                    )
                    loadLeaderboard(examResult.examSession)
                }
            }
        }
    }

    private fun switchResultTab(tab: ResultTab) {
        val current = _uiState.value as? ExamUiState.ResultSummary ?: return
        _uiState.value = current.copy(activeTab = tab)
        if (tab == ResultTab.LEADERBOARD && current.leaderboard.isEmpty()) {
            loadLeaderboard(current.result.examSession)
        }
    }

    private fun loadLeaderboard(examSession: String) {
        val current = _uiState.value as? ExamUiState.ResultSummary ?: return
        _uiState.value = current.copy(isLoadingLeaderboard = true)

        viewModelScope.launch {
            when (val result = getLeaderboardUseCase(examSession)) {
                is Result.Success -> {
                    _uiState.update { state ->
                        if (state !is ExamUiState.ResultSummary) return@update state
                        state.copy(
                            leaderboard = result.value,
                            isLoadingLeaderboard = false,
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        if (state !is ExamUiState.ResultSummary) return@update state
                        state.copy(isLoadingLeaderboard = false)
                    }
                }
            }
        }
    }

    private fun resetToSetup() {
        timerJob?.cancel()
        loadAvailableSessions()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
