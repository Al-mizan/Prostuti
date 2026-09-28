package com.prostuti.feature.history

import com.prostuti.core.common.Result
import com.prostuti.core.model.Option
import com.prostuti.core.model.SessionType
import com.prostuti.core.model.Subject
import com.prostuti.core.model.UserAttemptSummaryDto
import com.prostuti.core.model.WrongAnswerItemDto
import com.prostuti.feature.history.domain.GetUserAttemptsUseCase
import com.prostuti.feature.history.domain.GetWrongAnswersUseCase
import com.prostuti.feature.history.domain.HistoryRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryUseCasesTest {

    private class FakeHistoryRepository : HistoryRepository {
        val sampleAttempts = listOf(
            UserAttemptSummaryDto(
                id = "att-1",
                sessionType = SessionType.EXAM,
                title = "47th BCS Preliminary",
                score = 42,
                totalQuestions = 50,
                timeTakenSeconds = 1500,
                startedAt = "2026-09-29T10:00:00Z",
                finishedAt = "2026-09-29T10:25:00Z",
            ),
            UserAttemptSummaryDto(
                id = "att-2",
                sessionType = SessionType.PRACTICE,
                title = "বাংলা ভাষা ও সাহিত্য",
                subject = Subject.BENGALI,
                score = 8,
                totalQuestions = 10,
                startedAt = "2026-09-28T18:00:00Z",
                finishedAt = "2026-09-28T18:10:00Z",
            ),
        )

        val sampleWrongAnswers = listOf(
            WrongAnswerItemDto(
                answerId = "ans-1",
                sessionId = "att-1",
                sessionType = SessionType.EXAM,
                questionId = "q-1",
                subject = Subject.BENGALI,
                topic = "বাংলা ব্যাকরণ",
                questionText = "'সূর্য' শব্দের সমার্থক শব্দ কোনটি?",
                optionA = "শশাঙ্ক",
                optionB = "অদ্রি",
                optionC = "ভানু",
                optionD = "বিধু",
                selectedOption = Option.A,
                correctOption = Option.C,
                explanation = "'ভানু' অর্থ সূর্য। 'শশাঙ্ক' ও 'বিধু' অর্থ চাঁদ।",
                answeredAt = "2026-09-29T10:05:00Z",
            ),
            WrongAnswerItemDto(
                answerId = "ans-2",
                sessionId = "att-1",
                sessionType = SessionType.EXAM,
                questionId = "q-2",
                subject = Subject.MATH,
                topic = "শতকরা",
                questionText = "১০০ টাকার ১০% কত?",
                optionA = "৫",
                optionB = "১০",
                optionC = "১৫",
                optionD = "২০",
                selectedOption = Option.A,
                correctOption = Option.B,
                explanation = "১০০ এর ১০% = ১০ টাকা।",
                answeredAt = "2026-09-29T10:12:00Z",
            )
        )

        override suspend fun getAttempts(): Result<List<UserAttemptSummaryDto>> =
            Result.Success(sampleAttempts)

        override suspend fun getWrongAnswers(subject: Subject?): Result<List<WrongAnswerItemDto>> =
            if (subject == null) {
                Result.Success(sampleWrongAnswers)
            } else {
                Result.Success(sampleWrongAnswers.filter { it.subject == subject })
            }
    }

    @Test
    fun `GetUserAttemptsUseCase returns user attempts`() = runBlocking {
        val repo = FakeHistoryRepository()
        val useCase = GetUserAttemptsUseCase(repo)

        val result = useCase()
        assertTrue(result is Result.Success)
        val list = (result as Result.Success).value
        assertEquals(2, list.size)
        assertEquals("47th BCS Preliminary", list[0].title)
        assertEquals(SessionType.EXAM, list[0].sessionType)
        assertEquals(SessionType.PRACTICE, list[1].sessionType)
    }

    @Test
    fun `GetWrongAnswersUseCase returns all wrong answers when subject is null`() = runBlocking {
        val repo = FakeHistoryRepository()
        val useCase = GetWrongAnswersUseCase(repo)

        val result = useCase(subject = null)
        assertTrue(result is Result.Success)
        val list = (result as Result.Success).value
        assertEquals(2, list.size)
    }

    @Test
    fun `GetWrongAnswersUseCase filters by subject correctly`() = runBlocking {
        val repo = FakeHistoryRepository()
        val useCase = GetWrongAnswersUseCase(repo)

        val result = useCase(subject = Subject.BENGALI)
        assertTrue(result is Result.Success)
        val list = (result as Result.Success).value
        assertEquals(1, list.size)
        assertEquals(Subject.BENGALI, list[0].subject)
        assertEquals(Option.C, list[0].correctOption)
    }
}
