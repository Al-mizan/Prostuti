package com.prostuti.feature.practice.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prostuti.core.common.Result
import com.prostuti.core.model.Option
import com.prostuti.core.model.Subject
import com.prostuti.feature.practice.domain.FinishPracticeSessionUseCase
import com.prostuti.feature.practice.domain.StartPracticeSessionUseCase
import com.prostuti.feature.practice.domain.SubmitPracticeAnswerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PracticeViewModel(
    private val startPracticeSession: StartPracticeSessionUseCase,
    private val submitPracticeAnswer: SubmitPracticeAnswerUseCase,
    private val finishPracticeSession: FinishPracticeSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PracticeUiState>(PracticeUiState.SubjectSelect())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    fun onEvent(event: PracticeUiEvent) {
        when (event) {
            is PracticeUiEvent.ChangeQuestionCount -> {
                val current = _uiState.value as? PracticeUiState.SubjectSelect ?: return
                _uiState.value = current.copy(selectedCount = event.count)
            }
            is PracticeUiEvent.StartSession -> startSession(event.subject, event.count)
            is PracticeUiEvent.SubmitAnswer -> submitAnswer(event.option)
            is PracticeUiEvent.NextQuestion -> nextQuestion()
            is PracticeUiEvent.FinishSession -> finishSession()
            is PracticeUiEvent.ResetToSubjectSelect -> {
                _uiState.value = PracticeUiState.SubjectSelect()
            }
        }
    }

    private fun startSession(subject: Subject, count: Int) {
        _uiState.value = PracticeUiState.Loading("অনুশীলন সেশন তৈরি হচ্ছে...")
        viewModelScope.launch {
            when (val result = startPracticeSession(subject, count)) {
                is Result.Error -> {
                    _uiState.value = PracticeUiState.Error(result.message)
                }
                is Result.Success -> {
                    val session = result.value
                    _uiState.value = PracticeUiState.ActiveSession(
                        sessionId = session.id,
                        subject = session.subject,
                        questions = session.questions,
                        currentIndex = 0,
                    )
                }
            }
        }
    }

    private fun submitAnswer(option: Option) {
        val current = _uiState.value as? PracticeUiState.ActiveSession ?: return
        if (current.isCurrentQuestionAnswered || current.isSubmittingAnswer) return

        val questionId = current.currentQuestion.id
        _uiState.value = current.copy(isSubmittingAnswer = true)

        viewModelScope.launch {
            when (val result = submitPracticeAnswer(current.sessionId, questionId, option)) {
                is Result.Error -> {
                    _uiState.value = current.copy(isSubmittingAnswer = false)
                }
                is Result.Success -> {
                    _uiState.update { state ->
                        if (state !is PracticeUiState.ActiveSession) return@update state
                        state.copy(
                            answers = state.answers + (questionId to result.value),
                            isSubmittingAnswer = false,
                        )
                    }
                }
            }
        }
    }

    private fun nextQuestion() {
        val current = _uiState.value as? PracticeUiState.ActiveSession ?: return
        if (current.hasNextQuestion) {
            _uiState.value = current.copy(currentIndex = current.currentIndex + 1)
        } else {
            finishSession()
        }
    }

    private fun finishSession() {
        val current = _uiState.value as? PracticeUiState.ActiveSession ?: return
        if (current.isFinishing) return

        _uiState.value = current.copy(isFinishing = true)

        viewModelScope.launch {
            when (val result = finishPracticeSession(current.sessionId)) {
                is Result.Error -> {
                    _uiState.value = PracticeUiState.Error(result.message)
                }
                is Result.Success -> {
                    _uiState.value = PracticeUiState.SessionSummary(
                        summary = result.value,
                        questions = current.questions,
                        answers = current.answers,
                    )
                }
            }
        }
    }
}
