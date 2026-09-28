package com.prostuti.core.model

import kotlinx.serialization.Serializable

/** PRACTICE = practice session, EXAM = solo timed BCS mock test. */
@Serializable
enum class SessionType {
    PRACTICE,
    EXAM,
}
