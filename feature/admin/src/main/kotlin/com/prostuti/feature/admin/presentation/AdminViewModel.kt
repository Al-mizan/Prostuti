package com.prostuti.feature.admin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prostuti.core.common.Result
import com.prostuti.core.model.Role
import com.prostuti.core.model.UpdateQuestionRequest
import com.prostuti.feature.admin.domain.DeleteAdminQuestionUseCase
import com.prostuti.feature.admin.domain.GetAdminQuestionsUseCase
import com.prostuti.feature.admin.domain.GetAdminUsersUseCase
import com.prostuti.feature.admin.domain.ImportCsvUseCase
import com.prostuti.feature.admin.domain.UpdateAdminQuestionUseCase
import com.prostuti.feature.admin.domain.UpdateUserRoleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminViewModel(
    private val importCsvUseCase: ImportCsvUseCase,
    private val getAdminQuestionsUseCase: GetAdminQuestionsUseCase,
    private val updateAdminQuestionUseCase: UpdateAdminQuestionUseCase,
    private val deleteAdminQuestionUseCase: DeleteAdminQuestionUseCase,
    private val getAdminUsersUseCase: GetAdminUsersUseCase,
    private val updateUserRoleUseCase: UpdateUserRoleUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadQuestions(page = 0)
        loadUsers()
    }

    fun onEvent(event: AdminUiEvent) {
        when (event) {
            is AdminUiEvent.SelectTab -> {
                _uiState.update { it.copy(selectedTab = event.tab) }
                when (event.tab) {
                    AdminTab.QUESTIONS -> loadQuestions(_uiState.value.currentPage)
                    AdminTab.USERS -> loadUsers()
                    AdminTab.CSV_IMPORT -> Unit
                }
            }

            // CSV Import
            is AdminUiEvent.SelectTargetQuestionType -> {
                _uiState.update { it.copy(targetQuestionType = event.type) }
            }
            is AdminUiEvent.SelectCsvFile -> {
                _uiState.update {
                    it.copy(
                        selectedFileName = event.fileName,
                        selectedFileBytes = event.bytes,
                        importStatus = CsvImportStatus.Idle,
                    )
                }
            }
            is AdminUiEvent.ClearSelectedCsvFile -> {
                _uiState.update {
                    it.copy(
                        selectedFileName = null,
                        selectedFileBytes = null,
                        importStatus = CsvImportStatus.Idle,
                    )
                }
            }
            is AdminUiEvent.TriggerCsvImport -> importCsv()
            is AdminUiEvent.DismissImportResult -> {
                _uiState.update { it.copy(importStatus = CsvImportStatus.Idle) }
            }

            // Questions
            is AdminUiEvent.SetFilterType -> {
                _uiState.update { it.copy(filterType = event.type, currentPage = 0) }
                loadQuestions(0)
            }
            is AdminUiEvent.SetFilterSubject -> {
                _uiState.update { it.copy(filterSubject = event.subject, currentPage = 0) }
                loadQuestions(0)
            }
            is AdminUiEvent.SetFilterExamSession -> {
                _uiState.update { it.copy(filterExamSession = event.examSession, currentPage = 0) }
                loadQuestions(0)
            }
            is AdminUiEvent.ChangeQuestionsPage -> {
                _uiState.update { it.copy(currentPage = event.page) }
                loadQuestions(event.page)
            }
            is AdminUiEvent.OpenEditQuestionDialog -> {
                _uiState.update { it.copy(questionToEdit = event.question) }
            }
            is AdminUiEvent.DismissEditQuestionDialog -> {
                _uiState.update { it.copy(questionToEdit = null) }
            }
            is AdminUiEvent.SubmitEditQuestion -> editQuestion(event.id, event.request)
            is AdminUiEvent.RequestDeleteQuestion -> {
                _uiState.update { it.copy(questionToDelete = event.question) }
            }
            is AdminUiEvent.DismissDeleteQuestionDialog -> {
                _uiState.update { it.copy(questionToDelete = null) }
            }
            is AdminUiEvent.ConfirmDeleteQuestion -> deleteQuestion()
            is AdminUiEvent.RefreshQuestions -> loadQuestions(_uiState.value.currentPage)

            // Users
            is AdminUiEvent.RefreshUsers -> loadUsers()
            is AdminUiEvent.ToggleUserRole -> toggleUserRole(event.userId, event.currentRole)

            // User Message
            is AdminUiEvent.DismissUserMessage -> {
                _uiState.update { it.copy(userMessage = null) }
            }
        }
    }

    private fun importCsv() {
        val state = _uiState.value
        val fileName = state.selectedFileName
        val bytes = state.selectedFileBytes
        if (fileName.isNullOrBlank() || bytes == null || bytes.isEmpty()) {
            _uiState.update { it.copy(userMessage = "অনুগ্রহ করে একটি CSV ফাইল নির্বাচন করুন") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(importStatus = CsvImportStatus.Uploading) }
            val result = importCsvUseCase(
                type = state.targetQuestionType,
                fileName = fileName,
                bytes = bytes,
            )
            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            importStatus = CsvImportStatus.Success(result.value),
                            selectedFileName = null,
                            selectedFileBytes = null,
                            userMessage = "${result.value.imported}টি প্রশ্ন সফলভাবে ইমপোর্ট করা হয়েছে",
                        )
                    }
                    loadQuestions(0)
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            importStatus = CsvImportStatus.Error(result.message),
                            userMessage = "ইমপোর্ট ব্যর্থ হয়েছে: ${result.message}",
                        )
                    }
                }
            }
        }
    }

    private fun loadQuestions(page: Int) {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(questionsStatus = AdminQuestionsStatus.Loading) }
            val result = getAdminQuestionsUseCase(
                type = state.filterType,
                examSession = state.filterExamSession,
                subject = state.filterSubject,
                page = page,
                pageSize = 20,
            )
            when (result) {
                is Result.Success -> {
                    val pageData = result.value
                    _uiState.update {
                        it.copy(
                            questionsStatus = AdminQuestionsStatus.Success(
                                items = pageData.items,
                                page = pageData.page,
                                pageSize = pageData.pageSize,
                                total = pageData.total,
                            ),
                            currentPage = pageData.page,
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            questionsStatus = AdminQuestionsStatus.Error(result.message),
                        )
                    }
                }
            }
        }
    }

    private fun editQuestion(id: String, request: UpdateQuestionRequest) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingQuestion = true) }
            val result = updateAdminQuestionUseCase(id, request)
            _uiState.update { it.copy(isSavingQuestion = false) }
            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            questionToEdit = null,
                            userMessage = "প্রশ্নটি সফলভাবে আপডেট করা হয়েছে",
                        )
                    }
                    loadQuestions(_uiState.value.currentPage)
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(userMessage = "আপডেট ব্যর্থ হয়েছে: ${result.message}")
                    }
                }
            }
        }
    }

    private fun deleteQuestion() {
        val question = _uiState.value.questionToDelete ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingQuestion = true) }
            val result = deleteAdminQuestionUseCase(question.id)
            _uiState.update { it.copy(isDeletingQuestion = false) }
            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            questionToDelete = null,
                            userMessage = "প্রশ্নটি সফলভাবে মুছে ফেলা হয়েছে",
                        )
                    }
                    loadQuestions(_uiState.value.currentPage)
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            questionToDelete = null,
                            userMessage = "মুছে ফেলা যায়নি: ${result.message}",
                        )
                    }
                }
            }
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(usersStatus = AdminUsersStatus.Loading) }
            val result = getAdminUsersUseCase()
            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(usersStatus = AdminUsersStatus.Success(result.value))
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(usersStatus = AdminUsersStatus.Error(result.message))
                    }
                }
            }
        }
    }

    private fun toggleUserRole(userId: String, currentRole: Role) {
        val newRole = if (currentRole == Role.ADMIN) Role.STUDENT else Role.ADMIN
        viewModelScope.launch {
            _uiState.update { it.copy(updatingUserRoleId = userId) }
            val result = updateUserRoleUseCase(userId, newRole)
            _uiState.update { it.copy(updatingUserRoleId = null) }
            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            userMessage = "ব্যবহারকারীর রোল ${newRole.name}-এ পরিবর্তন করা হয়েছে",
                        )
                    }
                    loadUsers()
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(userMessage = "রোল পরিবর্তন ব্যর্থ: ${result.message}")
                    }
                }
            }
        }
    }
}
