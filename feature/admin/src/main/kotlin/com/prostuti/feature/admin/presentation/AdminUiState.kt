package com.prostuti.feature.admin.presentation

import com.prostuti.core.model.AdminQuestionDto
import com.prostuti.core.model.AdminUserDto
import com.prostuti.core.model.ImportSummary
import com.prostuti.core.model.QuestionType
import com.prostuti.core.model.Subject

enum class AdminTab(val title: String) {
    CSV_IMPORT("CSV ইমপোর্ট"),
    QUESTIONS("প্রশ্নসমূহ"),
    USERS("ব্যবহারকারী"),
}

sealed interface CsvImportStatus {
    data object Idle : CsvImportStatus
    data object Uploading : CsvImportStatus
    data class Success(val summary: ImportSummary) : CsvImportStatus
    data class Error(val message: String) : CsvImportStatus
}

sealed interface AdminQuestionsStatus {
    data object Loading : AdminQuestionsStatus
    data class Success(
        val items: List<AdminQuestionDto>,
        val page: Int,
        val pageSize: Int,
        val total: Int,
    ) : AdminQuestionsStatus
    data class Error(val message: String) : AdminQuestionsStatus
}

sealed interface AdminUsersStatus {
    data object Loading : AdminUsersStatus
    data class Success(val users: List<AdminUserDto>) : AdminUsersStatus
    data class Error(val message: String) : AdminUsersStatus
}

data class AdminUiState(
    val selectedTab: AdminTab = AdminTab.CSV_IMPORT,

    // CSV Import Tab
    val targetQuestionType: QuestionType = QuestionType.BANK,
    val selectedFileName: String? = null,
    val selectedFileBytes: ByteArray? = null,
    val importStatus: CsvImportStatus = CsvImportStatus.Idle,

    // Questions Tab
    val questionsStatus: AdminQuestionsStatus = AdminQuestionsStatus.Loading,
    val filterType: QuestionType? = null,
    val filterExamSession: String? = null,
    val filterSubject: Subject? = null,
    val currentPage: Int = 0,
    val questionToEdit: AdminQuestionDto? = null,
    val questionToDelete: AdminQuestionDto? = null,
    val isSavingQuestion: Boolean = false,
    val isDeletingQuestion: Boolean = false,

    // Users Tab
    val usersStatus: AdminUsersStatus = AdminUsersStatus.Loading,
    val updatingUserRoleId: String? = null,

    // Banner / Snackbar notification
    val userMessage: String? = null,
)
