package com.prostuti.feature.questionbank.domain

import com.prostuti.core.common.Result
import com.prostuti.core.model.BcsSessionSummaryDto
import com.prostuti.core.model.Page
import com.prostuti.core.model.QuestionBankItemDto
import com.prostuti.core.model.Subject

interface QuestionBankRepository {
    suspend fun getSessions(): Result<List<BcsSessionSummaryDto>>
    suspend fun getQuestions(
        session: String,
        subject: Subject?,
        page: Int,
        pageSize: Int,
    ): Result<Page<QuestionBankItemDto>>
}
