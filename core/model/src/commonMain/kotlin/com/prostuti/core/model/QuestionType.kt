package com.prostuti.core.model

import kotlinx.serialization.Serializable

/** BANK = BCS question bank, PRACTICE = practice mode. */
@Serializable
enum class QuestionType {
    BANK,
    PRACTICE,
}
