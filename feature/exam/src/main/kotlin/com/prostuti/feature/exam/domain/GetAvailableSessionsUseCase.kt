package com.prostuti.feature.exam.domain

import com.prostuti.core.common.Result
import com.prostuti.core.model.BcsSessionSummaryDto

class GetAvailableSessionsUseCase(private val repository: ExamRepository) {
    suspend operator fun invoke(): Result<List<BcsSessionSummaryDto>> =
        repository.getAvailableSessions()
}
