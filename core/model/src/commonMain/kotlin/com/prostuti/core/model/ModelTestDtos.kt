package com.prostuti.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class ModelTestStatus {
    UPCOMING,
    LIVE,
    EXPIRED,
}

@Serializable
data class ModelTestDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val examSession: String,
    val durationMinutes: Int,
    val totalMarks: Double,
    val totalQuestions: Int,
    val startTime: String,
    val endTime: String,
    val status: ModelTestStatus = ModelTestStatus.LIVE,
    val isPublished: Boolean = true,
)

@Serializable
data class CreateModelTestRequest(
    val title: String,
    val description: String? = null,
    val examSession: String,
    val durationMinutes: Int = 120,
    val totalMarks: Double = 200.0,
    val totalQuestions: Int = 200,
    val startTime: String,
    val endTime: String,
    val isPublished: Boolean = true,
)

@Serializable
data class UpdateModelTestRequest(
    val title: String? = null,
    val description: String? = null,
    val examSession: String? = null,
    val durationMinutes: Int? = null,
    val totalMarks: Double? = null,
    val totalQuestions: Int? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val isPublished: Boolean? = null,
)
