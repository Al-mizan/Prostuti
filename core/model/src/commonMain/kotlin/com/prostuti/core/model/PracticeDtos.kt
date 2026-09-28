package com.prostuti.core.model

import kotlinx.serialization.Serializable

@Serializable
data class StartPracticeSessionRequest(
    val subject: Subject,
    val count: Int = 10,
)

@Serializable
data class PracticeSessionQuestionDto(
    val id: String,
    val subject: Subject,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val topic: String? = null,
    val difficulty: Difficulty? = null,
)

@Serializable
data class PracticeSessionDto(
    val id: String,
    val subject: Subject,
    val questions: List<PracticeSessionQuestionDto>,
    val startedAt: String,
)

@Serializable
data class SubmitPracticeAnswerRequest(
    val questionId: String,
    val selectedOption: Option,
)

@Serializable
data class PracticeAnswerResultDto(
    val questionId: String,
    val selectedOption: Option,
    val isCorrect: Boolean,
    val correctOption: Option,
    val explanation: String? = null,
)

@Serializable
data class FinishPracticeSessionResponse(
    val sessionId: String,
    val subject: Subject,
    val totalQuestions: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val score: Int,
)
