package com.prostuti.feature.questionbank.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prostuti.core.common.Result
import com.prostuti.core.model.BcsSessionSummaryDto
import com.prostuti.core.model.Option
import com.prostuti.core.model.Subject
import com.prostuti.feature.questionbank.domain.GetBcsSessionsUseCase
import com.prostuti.feature.questionbank.domain.GetQuestionBankQuestionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuestionBankViewModel(
    private val getBcsSessions: GetBcsSessionsUseCase,
    private val getQuestions: GetQuestionBankQuestionsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuestionBankUiState>(QuestionBankUiState.Loading)
    val uiState: StateFlow<QuestionBankUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun onEvent(event: QuestionBankUiEvent) {
        when (event) {
            is QuestionBankUiEvent.SelectSession -> selectSession(event.sessionName)
            is QuestionBankUiEvent.SelectSubject -> selectSubject(event.subject)
            is QuestionBankUiEvent.SelectOption -> selectOption(event.questionId, event.option)
            is QuestionBankUiEvent.ToggleExplanation -> toggleExplanation(event.questionId)
            is QuestionBankUiEvent.ChangePage -> changePage(event.page)
            is QuestionBankUiEvent.Retry -> loadInitialData()
        }
    }

    private fun loadInitialData() {
        _uiState.value = QuestionBankUiState.Loading
        viewModelScope.launch {
            when (val sessionsResult = getBcsSessions()) {
                is Result.Error -> {
                    _uiState.value = QuestionBankUiState.Error(sessionsResult.message)
                }
                is Result.Success -> {
                    val sessions = sessionsResult.value
                    if (sessions.isEmpty()) {
                        _uiState.value = QuestionBankUiState.Success(
                            sessions = emptyList(),
                            selectedSession = "",
                            selectedSubject = null,
                            questions = emptyList(),
                            page = 0,
                            totalQuestions = 0,
                        )
                        return@launch
                    }
                    val initialSession = sessions.first().sessionName
                    loadQuestionsForSession(
                        sessions = sessions,
                        sessionName = initialSession,
                        subject = null,
                        page = 0,
                    )
                }
            }
        }
    }

    private fun selectSession(sessionName: String) {
        val current = _uiState.value as? QuestionBankUiState.Success ?: return
        if (current.selectedSession == sessionName) return
        loadQuestionsForSession(
            sessions = current.sessions,
            sessionName = sessionName,
            subject = current.selectedSubject,
            page = 0,
        )
    }

    private fun selectSubject(subject: Subject?) {
        val current = _uiState.value as? QuestionBankUiState.Success ?: return
        if (current.selectedSubject == subject) return
        loadQuestionsForSession(
            sessions = current.sessions,
            sessionName = current.selectedSession,
            subject = subject,
            page = 0,
        )
    }

    private fun changePage(page: Int) {
        val current = _uiState.value as? QuestionBankUiState.Success ?: return
        if (page < 0 || (current.totalPages > 0 && page >= current.totalPages)) return
        loadQuestionsForSession(
            sessions = current.sessions,
            sessionName = current.selectedSession,
            subject = current.selectedSubject,
            page = page,
        )
    }

    private fun loadQuestionsForSession(
        sessions: List<BcsSessionSummaryDto>,
        sessionName: String,
        subject: Subject?,
        page: Int,
    ) {
        val current = _uiState.value as? QuestionBankUiState.Success
        if (current != null) {
            _uiState.value = current.copy(isRefreshingQuestions = true)
        }

        viewModelScope.launch {
            when (val result = getQuestions(session = sessionName, subject = subject, page = page)) {
                is Result.Error -> {
                    _uiState.value = QuestionBankUiState.Error(result.message)
                }
                is Result.Success -> {
                    val pageData = result.value
                    _uiState.value = QuestionBankUiState.Success(
                        sessions = sessions,
                        selectedSession = sessionName,
                        selectedSubject = subject,
                        questions = pageData.items,
                        page = pageData.page,
                        pageSize = pageData.pageSize,
                        totalQuestions = pageData.total,
                        selectedOptions = emptyMap(),
                        expandedExplanations = emptySet(),
                        isRefreshingQuestions = false,
                    )
                }
            }
        }
    }

    private fun selectOption(questionId: String, option: Option) {
        _uiState.update { state ->
            if (state !is QuestionBankUiState.Success) return@update state
            state.copy(
                selectedOptions = state.selectedOptions + (questionId to option)
            )
        }
    }

    private fun toggleExplanation(questionId: String) {
        _uiState.update { state ->
            if (state !is QuestionBankUiState.Success) return@update state
            val nextSet = if (state.expandedExplanations.contains(questionId)) {
                state.expandedExplanations - questionId
            } else {
                state.expandedExplanations + questionId
            }
            state.copy(expandedExplanations = nextSet)
        }
    }
}
