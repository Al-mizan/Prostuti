package com.prostuti.feature.history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prostuti.core.common.Result
import com.prostuti.core.model.Subject
import com.prostuti.feature.history.domain.GetUserAttemptsUseCase
import com.prostuti.feature.history.domain.GetWrongAnswersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val getUserAttempts: GetUserAttemptsUseCase,
    private val getWrongAnswers: GetWrongAnswersUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: HistoryUiEvent) {
        when (event) {
            is HistoryUiEvent.SwitchTab -> {
                val current = _uiState.value as? HistoryUiState.Content ?: return
                _uiState.value = current.copy(activeTab = event.tab)
            }
            is HistoryUiEvent.FilterAttemptType -> {
                val current = _uiState.value as? HistoryUiState.Content ?: return
                _uiState.value = current.copy(selectedAttemptTypeFilter = event.filter)
            }
            is HistoryUiEvent.FilterWrongAnswerSubject -> {
                filterWrongAnswerSubject(event.subject)
            }
            is HistoryUiEvent.Refresh -> {
                loadData()
            }
        }
    }

    fun loadData() {
        val currentTab = (_uiState.value as? HistoryUiState.Content)?.activeTab ?: HistoryTab.ATTEMPTS
        _uiState.value = HistoryUiState.Loading

        viewModelScope.launch {
            val attemptsDeferred = getUserAttempts()
            val wrongAnswersDeferred = getWrongAnswers()

            val attempts = when (attemptsDeferred) {
                is Result.Success -> attemptsDeferred.value
                is Result.Error -> emptyList()
            }

            val wrongAnswers = when (wrongAnswersDeferred) {
                is Result.Success -> wrongAnswersDeferred.value
                is Result.Error -> emptyList()
            }

            if (attemptsDeferred is Result.Error && wrongAnswersDeferred is Result.Error) {
                _uiState.value = HistoryUiState.Error(attemptsDeferred.message)
            } else {
                _uiState.value = HistoryUiState.Content(
                    activeTab = currentTab,
                    allAttempts = attempts,
                    allWrongAnswers = wrongAnswers,
                )
            }
        }
    }

    private fun filterWrongAnswerSubject(subject: Subject?) {
        val current = _uiState.value as? HistoryUiState.Content ?: return
        _uiState.value = current.copy(
            selectedSubjectFilter = subject,
            isLoadingWrongAnswers = true,
        )

        viewModelScope.launch {
            when (val result = getWrongAnswers(subject)) {
                is Result.Success -> {
                    _uiState.update { state ->
                        if (state !is HistoryUiState.Content) return@update state
                        state.copy(
                            allWrongAnswers = result.value,
                            isLoadingWrongAnswers = false,
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        if (state !is HistoryUiState.Content) return@update state
                        state.copy(isLoadingWrongAnswers = false)
                    }
                }
            }
        }
    }
}
