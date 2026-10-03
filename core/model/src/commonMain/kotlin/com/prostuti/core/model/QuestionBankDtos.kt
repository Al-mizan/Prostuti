package com.prostuti.core.model

import kotlinx.serialization.Serializable

/**
 * Summary of a BCS Exam Session (e.g., "47th BCS Preliminary")
 * with total questions available in the question bank.
 */
@Serializable
data class BcsSessionSummaryDto(
    val sessionName: String,
    val totalQuestions: Int,
    val durationMinutes: Int = 120,
    val totalMarks: Double = 200.0,
    val negativeMarkingPerQuestion: Double = 0.5,
)

/**
 * DTO representing an individual Question Bank item for student study mode.
 */
@Serializable
data class QuestionBankItemDto(
    val id: String,
    val subject: Subject,
    val examSession: String,
    val topic: String? = null,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: Option,
    val explanation: String? = null,
    val difficulty: Difficulty? = null,
)
