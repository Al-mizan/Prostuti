package com.prostuti.feature.questionbank.domain

import com.prostuti.core.common.Result
import com.prostuti.core.model.BcsSessionSummaryDto
import com.prostuti.core.model.ModelTestDto
import com.prostuti.core.model.Page
import com.prostuti.core.model.QuestionBankItemDto
import com.prostuti.core.model.Subject

class GetBcsSessionsUseCase(
    private val repository: QuestionBankRepository,
) {
    suspend operator fun invoke(): Result<List<BcsSessionSummaryDto>> =
        repository.getSessions()
}

class GetQuestionBankQuestionsUseCase(
    private val repository: QuestionBankRepository,
) {
    suspend operator fun invoke(
        session: String,
        subject: Subject? = null,
        page: Int = 0,
        pageSize: Int = 20,
    ): Result<Page<QuestionBankItemDto>> =
        repository.getQuestions(session, subject, page, pageSize)
}

class GetLiveModelTestUseCase(
    private val repository: QuestionBankRepository,
) {
    suspend operator fun invoke(): Result<ModelTestDto?> =
        repository.getLiveModelTest()
}

class GetAllModelTestsUseCase(
    private val repository: QuestionBankRepository,
) {
    suspend operator fun invoke(status: String? = null): Result<List<ModelTestDto>> =
        repository.getAllModelTests(status)
}

