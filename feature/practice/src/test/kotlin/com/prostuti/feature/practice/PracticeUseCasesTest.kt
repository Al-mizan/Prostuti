package com.prostuti.feature.practice

import com.prostuti.core.common.Result
import com.prostuti.core.model.FinishPracticeSessionResponse
import com.prostuti.core.model.Option
import com.prostuti.core.model.PracticeAnswerResultDto
import com.prostuti.core.model.PracticeSessionDto
import com.prostuti.core.model.PracticeSessionQuestionDto
import com.prostuti.core.model.Subject
import com.prostuti.feature.practice.domain.FinishPracticeSessionUseCase
import com.prostuti.feature.practice.domain.PracticeRepository
import com.prostuti.feature.practice.domain.StartPracticeSessionUseCase
import com.prostuti.feature.practice.domain.SubmitPracticeAnswerUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PracticeUseCasesTest {

    private class FakePracticeRepository : PracticeRepository {
        val sampleQuestions = listOf(
            PracticeSessionQuestionDto(
                id = "pq-1",
                subject = Subject.MATH,
                questionText = "২ + ২ = ?",
                optionA = "৩",
                optionB = "৪",
                optionC = "৫",
                optionD = "৬",
            )
        )

        override suspend fun startSession(subject: Subject, count: Int): Result<PracticeSessionDto> =
            Result.Success(
                PracticeSessionDto(
                    id = "session-1",
                    subject = subject,
                    questions = sampleQuestions,
                    startedAt = "2026-09-29T00:00:00Z",
                )
            )

        override suspend fun submitAnswer(
            sessionId: String,
            questionId: String,
            selectedOption: Option,
        ): Result<PracticeAnswerResultDto> =
            Result.Success(
                PracticeAnswerResultDto(
                    questionId = questionId,
                    selectedOption = selectedOption,
                    isCorrect = (selectedOption == Option.B),
                    correctOption = Option.B,
                    explanation = "২ + ২ = ৪",
                )
            )

        override suspend fun finishSession(sessionId: String): Result<FinishPracticeSessionResponse> =
            Result.Success(
                FinishPracticeSessionResponse(
                    sessionId = sessionId,
                    subject = Subject.MATH,
                    totalQuestions = 1,
                    correctCount = 1,
                    incorrectCount = 0,
                    score = 1,
                )
            )
    }

    @Test
    fun `StartPracticeSessionUseCase returns practice session`() = runBlocking {
        val repo = FakePracticeRepository()
        val useCase = StartPracticeSessionUseCase(repo)

        val result = useCase(Subject.MATH, 10)
        assertTrue(result is Result.Success)
        val session = (result as Result.Success).value
        assertEquals("session-1", session.id)
        assertEquals(Subject.MATH, session.subject)
        assertEquals(1, session.questions.size)
    }

    @Test
    fun `SubmitPracticeAnswerUseCase evaluates answer correctly`() = runBlocking {
        val repo = FakePracticeRepository()
        val useCase = SubmitPracticeAnswerUseCase(repo)

        val result = useCase("session-1", "pq-1", Option.B)
        assertTrue(result is Result.Success)
        val answer = (result as Result.Success).value
        assertTrue(answer.isCorrect)
        assertEquals(Option.B, answer.correctOption)
    }

    @Test
    fun `FinishPracticeSessionUseCase returns summary`() = runBlocking {
        val repo = FakePracticeRepository()
        val useCase = FinishPracticeSessionUseCase(repo)

        val result = useCase("session-1")
        assertTrue(result is Result.Success)
        val summary = (result as Result.Success).value
        assertEquals(1, summary.score)
        assertEquals(1, summary.correctCount)
        assertEquals(0, summary.incorrectCount)
    }

    @Test
    fun `PracticeSessionQuestionDto supports examSession pill badge metadata`() {
        val question = PracticeSessionQuestionDto(
            id = "pq-bcs",
            subject = Subject.BENGALI,
            questionText = "চর্যাপদ কোন ছন্দে রচিত?",
            optionA = "মাত্রাবৃত্ত",
            optionB = "অক্ষরবৃত্ত",
            optionC = "স্বরমাত্রিক",
            optionD = "মুক্তক",
            examSession = "৪৪তম বিসিএস",
        )
        assertEquals("৪৪তম বিসিএস", question.examSession)
    }
}
