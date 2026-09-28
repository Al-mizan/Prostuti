package com.prostuti.feature.history.domain

import com.prostuti.core.common.Result
import com.prostuti.core.model.Subject
import com.prostuti.core.model.UserAttemptSummaryDto
import com.prostuti.core.model.WrongAnswerItemDto

interface HistoryRepository {
    suspend fun getAttempts(): Result<List<UserAttemptSummaryDto>>
    suspend fun getWrongAnswers(subject: Subject? = null): Result<List<WrongAnswerItemDto>>
}
