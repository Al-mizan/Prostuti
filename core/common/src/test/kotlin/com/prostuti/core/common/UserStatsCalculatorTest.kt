package com.prostuti.core.common

import com.prostuti.core.model.SessionType
import com.prostuti.core.model.UserAttemptSummaryDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class UserStatsCalculatorTest {

    private fun createAttempt(
        id: String,
        score: Int,
        totalQuestions: Int,
        finishedAt: String? = null,
        startedAt: String = "2026-10-05T10:00:00Z"
    ): UserAttemptSummaryDto {
        return UserAttemptSummaryDto(
            id = id,
            sessionType = SessionType.PRACTICE,
            title = "Test Attempt $id",
            score = score,
            totalQuestions = totalQuestions,
            timeTakenSeconds = 60,
            startedAt = startedAt,
            finishedAt = finishedAt ?: startedAt
        )
    }

    @Test
    fun calculate_emptyAttempts_returnsEmptyStats() {
        val stats = UserStatsCalculator.calculate(emptyList())
        assertEquals(0, stats.streakDays)
        assertEquals(0, stats.totalXp)
        assertEquals(0, stats.accuracyRate)
        assertEquals(7, stats.weeklyActivity.size)
        assertTrue(stats.weeklyActivity.all { !it })
    }

    @Test
    fun calculate_computesXpAndAccuracyCorrectly() {
        val attempts = listOf(
            createAttempt(id = "1", score = 8, totalQuestions = 10),
            createAttempt(id = "2", score = 12, totalQuestions = 15)
        )
        // Total score = 20 -> XP = 20 * 10 = 200 XP
        // Total questions = 25 -> Accuracy = (20 / 25) * 100 = 80%
        val stats = UserStatsCalculator.calculate(attempts)
        assertEquals(200, stats.totalXp)
        assertEquals(80, stats.accuracyRate)
    }

    @Test
    fun calculate_activeToday_calculatesConsecutiveStreak() {
        val today = LocalDate.of(2026, 10, 5) // Monday
        val attempts = listOf(
            createAttempt(id = "1", score = 5, totalQuestions = 5, finishedAt = "2026-10-05T09:00:00Z"),
            createAttempt(id = "2", score = 5, totalQuestions = 5, finishedAt = "2026-10-05T14:00:00Z"), // multiple today
            createAttempt(id = "3", score = 5, totalQuestions = 5, finishedAt = "2026-10-04T12:00:00Z"), // yesterday
            createAttempt(id = "4", score = 5, totalQuestions = 5, finishedAt = "2026-10-03T12:00:00Z"), // 2 days ago
            createAttempt(id = "5", score = 5, totalQuestions = 5, finishedAt = "2026-10-01T12:00:00Z")  // gap on Oct 2
        )

        val stats = UserStatsCalculator.calculate(attempts, referenceDate = today)
        // Consecutive days: Oct 5, Oct 4, Oct 3 = 3 days streak
        assertEquals(3, stats.streakDays)
    }

    @Test
    fun calculate_activeYesterdayNotToday_preservesStreak() {
        val today = LocalDate.of(2026, 10, 5) // Monday, no attempt yet today
        val attempts = listOf(
            createAttempt(id = "1", score = 5, totalQuestions = 5, finishedAt = "2026-10-04T12:00:00Z"), // yesterday
            createAttempt(id = "2", score = 5, totalQuestions = 5, finishedAt = "2026-10-03T12:00:00Z"), // 2 days ago
            createAttempt(id = "3", score = 5, totalQuestions = 5, finishedAt = "2026-10-02T12:00:00Z")  // 3 days ago
        )

        val stats = UserStatsCalculator.calculate(attempts, referenceDate = today)
        // Consecutive days from yesterday: Oct 4, Oct 3, Oct 2 = 3 days streak
        assertEquals(3, stats.streakDays)
    }

    @Test
    fun calculate_brokenStreak_returnsZero() {
        val today = LocalDate.of(2026, 10, 5)
        val attempts = listOf(
            // Last active was 2 days ago (Oct 3)
            createAttempt(id = "1", score = 5, totalQuestions = 5, finishedAt = "2026-10-03T12:00:00Z")
        )

        val stats = UserStatsCalculator.calculate(attempts, referenceDate = today)
        assertEquals(0, stats.streakDays)
    }

    @Test
    fun calculate_weeklyActivity_mapsSaturdayToFriday() {
        // Reference date: 2026-10-05 (Monday)
        // Current week starts on Saturday: 2026-10-03 (Sat)
        // Sat (Oct 3): index 0
        // Sun (Oct 4): index 1
        // Mon (Oct 5): index 2
        // Tue (Oct 6): index 3
        // Wed (Oct 7): index 4
        // Thu (Oct 8): index 5
        // Fri (Oct 9): index 6
        val today = LocalDate.of(2026, 10, 5)
        val attempts = listOf(
            createAttempt(id = "1", score = 10, totalQuestions = 10, finishedAt = "2026-10-03T10:00:00Z"), // Sat
            createAttempt(id = "2", score = 10, totalQuestions = 10, finishedAt = "2026-10-05T08:00:00Z")  // Mon
        )

        val stats = UserStatsCalculator.calculate(attempts, referenceDate = today)
        assertTrue("Saturday should be active", stats.weeklyActivity[0])
        assertFalse("Sunday should not be active", stats.weeklyActivity[1])
        assertTrue("Monday should be active", stats.weeklyActivity[2])
        assertFalse("Tuesday should not be active", stats.weeklyActivity[3])
        assertFalse("Wednesday should not be active", stats.weeklyActivity[4])
        assertFalse("Thursday should not be active", stats.weeklyActivity[5])
        assertFalse("Friday should not be active", stats.weeklyActivity[6])
    }
}
