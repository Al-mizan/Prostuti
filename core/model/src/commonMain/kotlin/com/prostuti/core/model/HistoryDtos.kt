package com.prostuti.core.model

import kotlinx.serialization.Serializable

@Serializable
data class UserAttemptSummaryDto(
    val id: String,
    val sessionType: SessionType,
    val title: String,
    val subject: Subject? = null,
    val examSession: String? = null,
    val score: Int,
    val totalQuestions: Int,
    val timeTakenSeconds: Int? = null,
    val startedAt: String,
    val finishedAt: String? = null,
)

@Serializable
data class WrongAnswerItemDto(
    val answerId: String,
    val sessionId: String,
    val sessionType: SessionType,
    val questionId: String,
    val subject: Subject,
    val topic: String? = null,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val selectedOption: Option,
    val correctOption: Option,
    val explanation: String? = null,
    val answeredAt: String,
)
