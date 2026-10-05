package com.prostuti.feature.questionbank.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prostuti.core.common.Result
import com.prostuti.core.model.BcsSessionSummaryDto
import com.prostuti.core.model.ModelTestDto
import com.prostuti.core.model.ModelTestStatus
import com.prostuti.core.model.Option
import com.prostuti.core.model.Subject
import com.prostuti.feature.questionbank.domain.GetAllModelTestsUseCase
import com.prostuti.feature.questionbank.domain.GetBcsSessionsUseCase
import com.prostuti.feature.questionbank.domain.GetLiveModelTestUseCase
import com.prostuti.feature.questionbank.domain.GetQuestionBankQuestionsUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuestionBankViewModel(
    private val getBcsSessions: GetBcsSessionsUseCase,
    private val getQuestions: GetQuestionBankQuestionsUseCase,
    private val getLiveModelTest: GetLiveModelTestUseCase,
    private val getAllModelTests: GetAllModelTestsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuestionBankUiState>(QuestionBankUiState.Loading)
    val uiState: StateFlow<QuestionBankUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun onEvent(event: QuestionBankUiEvent) {
        when (event) {
            is QuestionBankUiEvent.NavigateView -> navigateView(event.view)
            is QuestionBankUiEvent.OpenSubjectStudy -> openSubjectStudy(event.subject)
            is QuestionBankUiEvent.OpenSessionModal -> openSessionModal(event.session)
            is QuestionBankUiEvent.CloseSessionModal -> closeSessionModal()
            is QuestionBankUiEvent.UpdateSearchQuery -> updateSearchQuery(event.query)
            is QuestionBankUiEvent.SelectSession -> selectSession(event.sessionName)
            is QuestionBankUiEvent.SelectSubject -> selectSubject(event.subject)
            is QuestionBankUiEvent.SelectOption -> selectOption(event.questionId, event.option)
            is QuestionBankUiEvent.ToggleExplanation -> toggleExplanation(event.questionId)
            is QuestionBankUiEvent.ChangePage -> changePage(event.page)
            is QuestionBankUiEvent.Retry -> loadInitialData()
            is QuestionBankUiEvent.FilterModelTests -> filterModelTests(event.status)
            is QuestionBankUiEvent.RefreshModelTests -> refreshModelTests()
        }
    }

    private fun navigateView(view: QuestionBankView) {
        _uiState.update { state ->
            if (state is QuestionBankUiState.Success) state.copy(view = view) else state
        }
    }

    private fun openSessionModal(session: BcsSessionSummaryDto) {
        _uiState.update { state ->
            if (state is QuestionBankUiState.Success) state.copy(activeModalSession = session) else state
        }
    }

    private fun closeSessionModal() {
        _uiState.update { state ->
            if (state is QuestionBankUiState.Success) state.copy(activeModalSession = null) else state
        }
    }

    private fun updateSearchQuery(query: String) {
        _uiState.update { state ->
            if (state is QuestionBankUiState.Success) state.copy(searchQuery = query) else state
        }
    }

    private fun openSubjectStudy(subject: Subject) {
        val current = _uiState.value as? QuestionBankUiState.Success ?: return
        val sessionName = current.selectedSession.ifBlank {
            current.sessions.firstOrNull()?.sessionName ?: "47th BCS Preliminary"
        }
        loadQuestionsForSession(
            sessions = current.sessions,
            sessionName = sessionName,
            subject = subject,
            page = 0,
            overrideView = QuestionBankView.STUDY,
        )
    }

    fun viewQuestionsForSession(session: BcsSessionSummaryDto) {
        val current = _uiState.value as? QuestionBankUiState.Success ?: return
        loadQuestionsForSession(
            sessions = current.sessions,
            sessionName = session.sessionName,
            subject = null,
            page = 0,
            overrideView = QuestionBankView.STUDY,
        )
    }

    private fun loadInitialData() {
        _uiState.value = QuestionBankUiState.Loading
        viewModelScope.launch {
            val sessionsDeferred = async { getBcsSessions() }
            val liveTestDeferred = async { getLiveModelTest() }
            val allTestsDeferred = async { getAllModelTests() }

            val sessionsResult = sessionsDeferred.await()
            val liveTestResult = liveTestDeferred.await()
            val allTestsResult = allTestsDeferred.await()

            val liveModelTest = (liveTestResult as? Result.Success)?.value
            val modelTests = (allTestsResult as? Result.Success)?.value ?: emptyList()

            when (sessionsResult) {
                is Result.Error -> {
                    _uiState.value = QuestionBankUiState.Error(sessionsResult.message)
                }
                is Result.Success -> {
                    val sessions = sessionsResult.value
                    if (sessions.isEmpty()) {
                        _uiState.value = QuestionBankUiState.Success(
                            view = QuestionBankView.HOME,
                            sessions = emptyList(),
                            selectedSession = "",
                            selectedSubject = null,
                            questions = emptyList(),
                            page = 0,
                            totalQuestions = 0,
                            liveModelTest = liveModelTest,
                            modelTests = modelTests,
                        )
                        return@launch
                    }
                    val initialSession = sessions.first().sessionName
                    loadQuestionsForSession(
                        sessions = sessions,
                        sessionName = initialSession,
                        subject = null,
                        page = 0,
                        overrideView = QuestionBankView.HOME,
                        liveModelTest = liveModelTest,
                        modelTests = modelTests,
                    )
                }
            }
        }
    }

    private fun filterModelTests(status: ModelTestStatus?) {
        _uiState.update { state ->
            if (state is QuestionBankUiState.Success) state.copy(selectedModelTestFilter = status) else state
        }
    }

    private fun refreshModelTests() {
        viewModelScope.launch {
            val current = _uiState.value as? QuestionBankUiState.Success ?: return@launch
            _uiState.update { if (it is QuestionBankUiState.Success) it.copy(isLoadingModelTests = true) else it }
            val liveTestResult = getLiveModelTest()
            val allTestsResult = getAllModelTests()
            val liveModelTest = (liveTestResult as? Result.Success)?.value ?: current.liveModelTest
            val modelTests = (allTestsResult as? Result.Success)?.value ?: current.modelTests
            _uiState.update { state ->
                if (state is QuestionBankUiState.Success) {
                    state.copy(
                        liveModelTest = liveModelTest,
                        modelTests = modelTests,
                        isLoadingModelTests = false,
                    )
                } else state
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
        overrideView: QuestionBankView? = null,
        liveModelTest: ModelTestDto? = null,
        modelTests: List<ModelTestDto>? = null,
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
                        view = overrideView ?: current?.view ?: QuestionBankView.HOME,
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
                        activeModalSession = null,
                        searchQuery = current?.searchQuery ?: "",
                        liveModelTest = liveModelTest ?: current?.liveModelTest,
                        modelTests = modelTests ?: current?.modelTests ?: emptyList(),
                        isLoadingModelTests = current?.isLoadingModelTests ?: false,
                        selectedModelTestFilter = current?.selectedModelTestFilter,
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
