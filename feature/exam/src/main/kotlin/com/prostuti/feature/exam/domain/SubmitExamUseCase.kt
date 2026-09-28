package com.prostuti.feature.exam.domain

import com.prostuti.core.common.Result
import com.prostuti.core.model.ExamAnswerSubmission
import com.prostuti.core.model.ExamResultDto

class SubmitExamUseCase(private val repository: ExamRepository) {
    suspend operator fun invoke(
        sessionId: String,
        answers: List<ExamAnswerSubmission>,
        timeTakenSeconds: Int,
    ): Result<ExamResultDto> =
        repository.submitExam(sessionId, answers, timeTakenSeconds)
}
