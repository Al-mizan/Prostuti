package com.prostuti.core.model

import kotlinx.serialization.Serializable

@Serializable
data class StartExamSessionRequest(
    val examSession: String,
    val questionCount: Int = 50,
    val durationMinutes: Int = 30,
)

@Serializable
data class ExamQuestionDto(
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
data class ExamSessionDto(
    val id: String,
    val examSession: String,
    val totalQuestions: Int,
    val durationMinutes: Int,
    val questions: List<ExamQuestionDto>,
    val startedAt: String,
)

@Serializable
data class ExamAnswerSubmission(
    val questionId: String,
    val selectedOption: Option,
)

@Serializable
data class SubmitExamRequest(
    val answers: List<ExamAnswerSubmission>,
    val timeTakenSeconds: Int,
)

@Serializable
data class ExamQuestionResultDto(
    val questionId: String,
    val subject: Subject,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val selectedOption: Option? = null,
    val correctOption: Option,
    val isCorrect: Boolean,
    val explanation: String? = null,
    val topic: String? = null,
)

@Serializable
data class ExamResultDto(
    val sessionId: String,
    val examSession: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val skippedCount: Int,
    val score: Double,
    val timeTakenSeconds: Int,
    val questions: List<ExamQuestionResultDto>,
)

@Serializable
data class LeaderboardEntryDto(
    val rank: Int,
    val userId: String,
    val userName: String,
    val avatarId: String? = null,
    val score: Double,
    val timeTakenSeconds: Int,
    val finishedAt: String,
)
