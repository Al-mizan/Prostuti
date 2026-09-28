package com.prostuti.feature.exam

import com.prostuti.core.common.Result
import com.prostuti.core.model.BcsSessionSummaryDto
import com.prostuti.core.model.ExamAnswerSubmission
import com.prostuti.core.model.ExamQuestionDto
import com.prostuti.core.model.ExamQuestionResultDto
import com.prostuti.core.model.ExamResultDto
import com.prostuti.core.model.ExamSessionDto
import com.prostuti.core.model.LeaderboardEntryDto
import com.prostuti.core.model.Option
import com.prostuti.core.model.Subject
import com.prostuti.feature.exam.domain.ExamRepository
import com.prostuti.feature.exam.domain.GetAvailableSessionsUseCase
import com.prostuti.feature.exam.domain.GetLeaderboardUseCase
import com.prostuti.feature.exam.domain.StartExamSessionUseCase
import com.prostuti.feature.exam.domain.SubmitExamUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExamUseCasesTest {

    private class FakeExamRepository : ExamRepository {
        val sampleQuestions = listOf(
            ExamQuestionDto(
                id = "eq-1",
                subject = Subject.BENGALI,
                questionText = "'গীতাঞ্জলি' কার রচনা?",
                optionA = "কাজী নজরুল ইসলাম",
                optionB = "রবীন্দ্রনাথ ঠাকুর",
                optionC = "মাইকেল মধুসূদন দত্ত",
                optionD = "জসীমউদ্দীন",
                topic = "বাংলা সাহিত্য",
            ),
            ExamQuestionDto(
                id = "eq-2",
                subject = Subject.MATH,
                questionText = "১০ + ১০ = কত?",
                optionA = "১০",
                optionB = "২০",
                optionC = "৩০",
                optionD = "৪০",
                topic = "পাটিগণিত",
            )
        )

        override suspend fun getAvailableSessions(): Result<List<BcsSessionSummaryDto>> =
            Result.Success(
                listOf(
                    BcsSessionSummaryDto(sessionName = "47th BCS Preliminary", totalQuestions = 200),
                    BcsSessionSummaryDto(sessionName = "46th BCS Preliminary", totalQuestions = 200),
                )
            )

        override suspend fun startSession(
            examSession: String,
            questionCount: Int,
            durationMinutes: Int,
        ): Result<ExamSessionDto> =
            Result.Success(
                ExamSessionDto(
                    id = "exam-session-123",
                    examSession = examSession,
                    totalQuestions = sampleQuestions.size,
                    durationMinutes = durationMinutes,
                    questions = sampleQuestions,
                    startedAt = "2026-09-29T00:00:00Z",
                )
            )

        override suspend fun submitExam(
            sessionId: String,
            answers: List<ExamAnswerSubmission>,
            timeTakenSeconds: Int,
        ): Result<ExamResultDto> {
            val answersMap = answers.associateBy { it.questionId }
            val q1Answer = answersMap["eq-1"]?.selectedOption
            val q2Answer = answersMap["eq-2"]?.selectedOption

            val q1Correct = (q1Answer == Option.B)
            val q2Correct = (q2Answer == Option.B)

            val correctCount = (if (q1Correct) 1 else 0) + (if (q2Correct) 1 else 0)
            val incorrectCount = (if (q1Answer != null && !q1Correct) 1 else 0) + (if (q2Answer != null && !q2Correct) 1 else 0)
            val skippedCount = (if (q1Answer == null) 1 else 0) + (if (q2Answer == null) 1 else 0)
            val netScore = (correctCount.toDouble() - (incorrectCount * 0.5)).coerceAtLeast(0.0)

            return Result.Success(
                ExamResultDto(
                    sessionId = sessionId,
                    examSession = "47th BCS Preliminary",
                    totalQuestions = 2,
                    correctCount = correctCount,
                    incorrectCount = incorrectCount,
                    skippedCount = skippedCount,
                    score = netScore,
                    timeTakenSeconds = timeTakenSeconds,
                    questions = listOf(
                        ExamQuestionResultDto(
                            questionId = "eq-1",
                            subject = Subject.BENGALI,
                            questionText = "'গীতাঞ্জলি' কার রচনা?",
                            optionA = "কাজী নজরুল ইসলাম",
                            optionB = "রবীন্দ্রনাথ ঠাকুর",
                            optionC = "মাইকেল মধুসূদন দত্ত",
                            optionD = "জসীমউদ্দীন",
                            selectedOption = q1Answer,
                            correctOption = Option.B,
                            isCorrect = q1Correct,
                            explanation = "১৯১৩ সালে গীতাঞ্জলির জন্য তিনি নোবেল পুরস্কার পান।",
                        ),
                        ExamQuestionResultDto(
                            questionId = "eq-2",
                            subject = Subject.MATH,
                            questionText = "১০ + ১০ = কত?",
                            optionA = "১০",
                            optionB = "২০",
                            optionC = "৩০",
                            optionD = "৪০",
                            selectedOption = q2Answer,
                            correctOption = Option.B,
                            isCorrect = q2Correct,
                            explanation = "১০ + ১০ = ২০",
                        ),
                    ),
                )
            )
        }

        override suspend fun getLeaderboard(examSession: String): Result<List<LeaderboardEntryDto>> =
            Result.Success(
                listOf(
                    LeaderboardEntryDto(
                        rank = 1,
                        userId = "user-1",
                        userName = "আল মিজান",
                        score = 2.0,
                        timeTakenSeconds = 120,
                        finishedAt = "2026-09-29T00:10:00Z",
                    )
                )
            )
    }

    @Test
    fun `GetAvailableSessionsUseCase returns sessions list`() = runBlocking {
        val repo = FakeExamRepository()
        val useCase = GetAvailableSessionsUseCase(repo)

        val result = useCase()
        assertTrue(result is Result.Success)
        val sessions = (result as Result.Success).value
        assertEquals(2, sessions.size)
        assertEquals("47th BCS Preliminary", sessions[0].sessionName)
    }

    @Test
    fun `StartExamSessionUseCase returns active session`() = runBlocking {
        val repo = FakeExamRepository()
        val useCase = StartExamSessionUseCase(repo)

        val result = useCase(examSession = "47th BCS Preliminary", questionCount = 50, durationMinutes = 30)
        assertTrue(result is Result.Success)
        val session = (result as Result.Success).value
        assertEquals("exam-session-123", session.id)
        assertEquals("47th BCS Preliminary", session.examSession)
        assertEquals(30, session.durationMinutes)
        assertEquals(2, session.questions.size)
    }

    @Test
    fun `SubmitExamUseCase calculates score with BCS negative marks accurately`() = runBlocking {
        val repo = FakeExamRepository()
        val useCase = SubmitExamUseCase(repo)

        // 1 correct (eq-1 = B), 1 wrong (eq-2 = A, correct is B)
        // Score: 1 - (1 * 0.5) = 0.5
        val answers = listOf(
            ExamAnswerSubmission(questionId = "eq-1", selectedOption = Option.B),
            ExamAnswerSubmission(questionId = "eq-2", selectedOption = Option.A),
        )

        val result = useCase(sessionId = "exam-session-123", answers = answers, timeTakenSeconds = 240)
        assertTrue(result is Result.Success)
        val examResult = (result as Result.Success).value
        assertEquals(2, examResult.totalQuestions)
        assertEquals(1, examResult.correctCount)
        assertEquals(1, examResult.incorrectCount)
        assertEquals(0, examResult.skippedCount)
        assertEquals(0.5, examResult.score, 0.001)
        assertEquals(240, examResult.timeTakenSeconds)
    }

    @Test
    fun `GetLeaderboardUseCase returns ranked list`() = runBlocking {
        val repo = FakeExamRepository()
        val useCase = GetLeaderboardUseCase(repo)

        val result = useCase("47th BCS Preliminary")
        assertTrue(result is Result.Success)
        val list = (result as Result.Success).value
        assertEquals(1, list.size)
        assertEquals(1, list[0].rank)
        assertEquals("আল মিজান", list[0].userName)
    }
}
