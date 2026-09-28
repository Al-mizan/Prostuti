package com.prostuti.feature.history.domain

import com.prostuti.core.common.Result
import com.prostuti.core.model.UserAttemptSummaryDto

class GetUserAttemptsUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke(): Result<List<UserAttemptSummaryDto>> =
        repository.getAttempts()
}
