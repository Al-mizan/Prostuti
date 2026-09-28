package com.prostuti.feature.practice.domain

import com.prostuti.core.common.Result
import com.prostuti.core.model.FinishPracticeSessionResponse
import com.prostuti.core.model.Option
import com.prostuti.core.model.PracticeAnswerResultDto
import com.prostuti.core.model.PracticeSessionDto
import com.prostuti.core.model.Subject

interface PracticeRepository {
    suspend fun startSession(subject: Subject, count: Int): Result<PracticeSessionDto>
    suspend fun submitAnswer(
        sessionId: String,
        questionId: String,
        selectedOption: Option,
    ): Result<PracticeAnswerResultDto>
    suspend fun finishSession(sessionId: String): Result<FinishPracticeSessionResponse>
}
