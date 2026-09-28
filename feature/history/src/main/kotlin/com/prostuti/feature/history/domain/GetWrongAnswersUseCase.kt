package com.prostuti.feature.history.domain

import com.prostuti.core.common.Result
import com.prostuti.core.model.Subject
import com.prostuti.core.model.WrongAnswerItemDto

class GetWrongAnswersUseCase(private val repository: HistoryRepository) {
    suspend operator fun invoke(subject: Subject? = null): Result<List<WrongAnswerItemDto>> =
        repository.getWrongAnswers(subject)
}
