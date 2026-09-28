package com.prostuti.feature.practice.domain

import com.prostuti.core.common.Result
import com.prostuti.core.model.FinishPracticeSessionResponse
import com.prostuti.core.model.Option
import com.prostuti.core.model.PracticeAnswerResultDto
import com.prostuti.core.model.PracticeSessionDto
import com.prostuti.core.model.Subject

class StartPracticeSessionUseCase(
    private val repository: PracticeRepository,
) {
    suspend operator fun invoke(subject: Subject, count: Int = 10): Result<PracticeSessionDto> =
        repository.startSession(subject, count)
}

class SubmitPracticeAnswerUseCase(
    private val repository: PracticeRepository,
) {
    suspend operator fun invoke(
        sessionId: String,
        questionId: String,
        selectedOption: Option,
    ): Result<PracticeAnswerResultDto> =
        repository.submitAnswer(sessionId, questionId, selectedOption)
}

class FinishPracticeSessionUseCase(
    private val repository: PracticeRepository,
) {
    suspend operator fun invoke(sessionId: String): Result<FinishPracticeSessionResponse> =
        repository.finishSession(sessionId)
}
