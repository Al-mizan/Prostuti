package com.prostuti.core.model

import kotlinx.serialization.Serializable

/**
 * The 9 fixed BCS practice subjects.
 * This list is a closed set — do not add, remove, or rename entries
 * without explicit confirmation against docs/project_guide.md §4.
 */
@Serializable
enum class Subject {
    BENGALI,
    ENGLISH,
    BD_INTERNATIONAL_AFFAIRS,
    GEOGRAPHY,
    SCIENCE,
    IT,
    MATH,
    MENTAL_ABILITY,
    ETHICS,
}
