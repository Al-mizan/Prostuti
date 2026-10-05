package com.prostuti.core.common

import com.prostuti.core.model.UserAttemptSummaryDto
import java.time.DayOfWeek
import java.time.LocalDate

data class UserStats(
    val streakDays: Int,
    val totalXp: Int,
    val accuracyRate: Int,
    val weeklyActivity: List<Boolean>, // 7 days corresponding to Sat..Fri
) {
    companion object {
        val Empty = UserStats(
            streakDays = 0,
            totalXp = 0,
            accuracyRate = 0,
            weeklyActivity = listOf(false, false, false, false, false, false, false),
        )
    }
}

object UserStatsCalculator {

    /**
     * Calculates user stats from the historical list of practice and exam attempts.
     *
     * @param attempts list of user attempt summaries from GET /api/v1/history/attempts
     * @param referenceDate current date (defaults to LocalDate.now()) for streak & weekly calculation
     */
    fun calculate(
        attempts: List<UserAttemptSummaryDto>,
        referenceDate: LocalDate = LocalDate.now(),
    ): UserStats {
        if (attempts.isEmpty()) {
            return UserStats.Empty
        }

        // 1. Total XP: each correct answer / score point = 10 XP
        val totalCorrect = attempts.sumOf { maxOf(0, it.score) }
        val totalXp = totalCorrect * 10

        // 2. Accuracy Rate: (total score / total questions) * 100%
        val totalQuestions = attempts.sumOf { it.totalQuestions }
        val accuracyRate = if (totalQuestions > 0) {
            ((totalCorrect.toDouble() / totalQuestions.toDouble()) * 100.0).toInt().coerceIn(0, 100)
        } else {
            0
        }

        // 3. Active dates: extract distinct dates from completed attempts
        val activeDates = attempts
            .asSequence()
            .mapNotNull { it.finishedAt ?: it.startedAt }
            .filter { it.isNotBlank() }
            .mapNotNull { raw ->
                runCatching {
                    // Extract YYYY-MM-DD from ISO-8601 strings
                    val datePart = raw.take(10)
                    LocalDate.parse(datePart)
                }.getOrNull()
            }
            .toSet()

        // 4. Consecutive day streak
        // If user practiced today, count consecutive days backward from today.
        // If not today, but practiced yesterday, count consecutive days backward from yesterday.
        // Otherwise streak is 0.
        val streak = when {
            referenceDate in activeDates -> countStreakBackward(referenceDate, activeDates)
            referenceDate.minusDays(1) in activeDates -> countStreakBackward(referenceDate.minusDays(1), activeDates)
            else -> 0
        }

        // 5. Weekly activity: 7 days starting from Saturday (Bengali standard week: শনি, রবি, সোম, মঙ্গল, বুধ, বৃহঃ, শুক্র)
        val daysSinceSaturday = when (referenceDate.dayOfWeek) {
            DayOfWeek.SATURDAY -> 0
            DayOfWeek.SUNDAY -> 1
            DayOfWeek.MONDAY -> 2
            DayOfWeek.TUESDAY -> 3
            DayOfWeek.WEDNESDAY -> 4
            DayOfWeek.THURSDAY -> 5
            DayOfWeek.FRIDAY -> 6
            null -> 0
        }
        val saturday = referenceDate.minusDays(daysSinceSaturday.toLong())
        val weeklyActivity = (0..6).map { offset ->
            saturday.plusDays(offset.toLong()) in activeDates
        }

        return UserStats(
            streakDays = streak,
            totalXp = totalXp,
            accuracyRate = accuracyRate,
            weeklyActivity = weeklyActivity,
        )
    }

    private fun countStreakBackward(startDate: LocalDate, activeDates: Set<LocalDate>): Int {
        var count = 0
        var current = startDate
        while (current in activeDates) {
            count++
            current = current.minusDays(1)
        }
        return count
    }
}
