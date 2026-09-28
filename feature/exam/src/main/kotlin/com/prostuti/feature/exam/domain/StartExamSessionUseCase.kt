package com.prostuti.feature.exam.domain

import com.prostuti.core.common.Result
import com.prostuti.core.model.ExamSessionDto

class StartExamSessionUseCase(private val repository: ExamRepository) {
    suspend operator fun invoke(
        examSession: String,
        questionCount: Int = 50,
        durationMinutes: Int = 30,
    ): Result<ExamSessionDto> =
        repository.startSession(examSession, questionCount, durationMinutes)
}
