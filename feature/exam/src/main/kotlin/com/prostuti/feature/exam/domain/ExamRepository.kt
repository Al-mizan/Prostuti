package com.prostuti.feature.exam.domain

import com.prostuti.core.common.Result
import com.prostuti.core.model.BcsSessionSummaryDto
import com.prostuti.core.model.ExamAnswerSubmission
import com.prostuti.core.model.ExamResultDto
import com.prostuti.core.model.ExamSessionDto
import com.prostuti.core.model.LeaderboardEntryDto

interface ExamRepository {
    suspend fun getAvailableSessions(): Result<List<BcsSessionSummaryDto>>
    suspend fun startSession(examSession: String, questionCount: Int, durationMinutes: Int): Result<ExamSessionDto>
    suspend fun submitExam(sessionId: String, answers: List<ExamAnswerSubmission>, timeTakenSeconds: Int): Result<ExamResultDto>
    suspend fun getLeaderboard(examSession: String): Result<List<LeaderboardEntryDto>>
}
