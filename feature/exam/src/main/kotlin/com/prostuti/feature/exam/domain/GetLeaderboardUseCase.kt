package com.prostuti.feature.exam.domain

import com.prostuti.core.common.Result
import com.prostuti.core.model.LeaderboardEntryDto

class GetLeaderboardUseCase(private val repository: ExamRepository) {
    suspend operator fun invoke(examSession: String): Result<List<LeaderboardEntryDto>> =
        repository.getLeaderboard(examSession)
}
