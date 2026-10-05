package com.prostuti.feature.questionbank

import com.prostuti.core.common.Result
import com.prostuti.core.model.BcsSessionSummaryDto
import com.prostuti.core.model.ModelTestDto
import com.prostuti.core.model.ModelTestStatus
import com.prostuti.core.model.Option
import com.prostuti.core.model.Page
import com.prostuti.core.model.QuestionBankItemDto
import com.prostuti.core.model.Subject
import com.prostuti.feature.questionbank.domain.GetAllModelTestsUseCase
import com.prostuti.feature.questionbank.domain.GetBcsSessionsUseCase
import com.prostuti.feature.questionbank.domain.GetLiveModelTestUseCase
import com.prostuti.feature.questionbank.domain.GetQuestionBankQuestionsUseCase
import com.prostuti.feature.questionbank.domain.QuestionBankRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestionBankUseCasesTest {

    private class FakeQuestionBankRepository : QuestionBankRepository {
        val sampleSessions = listOf(
            BcsSessionSummaryDto("47th BCS Preliminary", 198),
            BcsSessionSummaryDto("46th BCS Preliminary", 198),
        )

        val sampleQuestions = listOf(
            QuestionBankItemDto(
                id = "q-1",
                subject = Subject.BENGALI,
                examSession = "47th BCS Preliminary",
                questionText = "'শ্রীকৃষ্ণকীর্তন' কাব্যের অংশ নয় কোনটি?",
                optionA = "নৌকা খণ্ড",
                optionB = "হার খণ্ড",
                optionC = "রাধা বিরহ",
                optionD = "প্রণয় খণ্ড",
                correctOption = Option.D,
                explanation = "শ্রীকৃষ্ণকীর্তন কাব্যে মোট ১৩টি খণ্ড রয়েছে...",
            )
        )

        val sampleModelTests = listOf(
            ModelTestDto(
                id = "mt-1",
                title = "৪৭তম বিসিএস বিশেষ লাইভ মডেল টেস্ট",
                description = "পূর্ণাঙ্গ সিলেবাস",
                examSession = "47th BCS Preliminary",
                durationMinutes = 120,
                totalMarks = 200.0,
                totalQuestions = 200,
                startTime = "2026-10-01T00:00:00Z",
                endTime = "2026-10-20T00:00:00Z",
                status = ModelTestStatus.LIVE,
            )
        )

        override suspend fun getSessions(): Result<List<BcsSessionSummaryDto>> =
            Result.Success(sampleSessions)

        override suspend fun getQuestions(
            session: String,
            subject: Subject?,
            page: Int,
            pageSize: Int,
        ): Result<Page<QuestionBankItemDto>> =
            Result.Success(Page(sampleQuestions, page, pageSize, sampleQuestions.size))

        override suspend fun getLiveModelTest(): Result<ModelTestDto?> =
            Result.Success(sampleModelTests.firstOrNull { it.status == ModelTestStatus.LIVE })

        override suspend fun getAllModelTests(status: String?): Result<List<ModelTestDto>> =
            Result.Success(sampleModelTests)
    }

    @Test
    fun `GetLiveModelTestUseCase returns active live model test`() = runBlocking {
        val repo = FakeQuestionBankRepository()
        val useCase = GetLiveModelTestUseCase(repo)

        val result = useCase()
        assertTrue(result is Result.Success)
        val liveTest = (result as Result.Success).value
        assertNotNull(liveTest)
        assertEquals("mt-1", liveTest?.id)
        assertEquals(ModelTestStatus.LIVE, liveTest?.status)
    }

    @Test
    fun `GetAllModelTestsUseCase returns all model tests`() = runBlocking {
        val repo = FakeQuestionBankRepository()
        val useCase = GetAllModelTestsUseCase(repo)

        val result = useCase()
        assertTrue(result is Result.Success)
        val list = (result as Result.Success).value
        assertEquals(1, list.size)
        assertEquals("৪৭তম বিসিএস বিশেষ লাইভ মডেল টেস্ট", list.first().title)
    }

    @Test
    fun `GetBcsSessionsUseCase returns session list from repository`() = runBlocking {
        val repo = FakeQuestionBankRepository()
        val useCase = GetBcsSessionsUseCase(repo)

        val result = useCase()
        assertTrue(result is Result.Success)
        val sessions = (result as Result.Success).value
        assertEquals(2, sessions.size)
        assertEquals("47th BCS Preliminary", sessions.first().sessionName)
        assertEquals(198, sessions.first().totalQuestions)
    }

    @Test
    fun `GetQuestionBankQuestionsUseCase returns paginated questions`() = runBlocking {
        val repo = FakeQuestionBankRepository()
        val useCase = GetQuestionBankQuestionsUseCase(repo)

        val result = useCase(session = "47th BCS Preliminary", subject = Subject.BENGALI, page = 0, pageSize = 20)
        assertTrue(result is Result.Success)
        val page = (result as Result.Success).value
        assertEquals(1, page.items.size)
        assertEquals("q-1", page.items.first().id)
        assertEquals(Option.D, page.items.first().correctOption)
    }

    @Test
    fun `BcsSessionSummaryDto retains accurate duration, marks, and negative marking defaults`() {
        val dto = BcsSessionSummaryDto(
            sessionName = "45th BCS Preliminary",
            totalQuestions = 200,
        )
        assertEquals(120, dto.durationMinutes)
        assertEquals(200.0, dto.totalMarks, 0.001)
        assertEquals(0.5, dto.negativeMarkingPerQuestion, 0.001)
    }

    @Test
    fun `BcsSubjectCards has exactly 9 subjects corresponding to Subject enum`() {
        val mappedSubjects = com.prostuti.feature.questionbank.ui.BcsSubjectCards.map { it.subject }.toSet()
        assertEquals(9, com.prostuti.feature.questionbank.ui.BcsSubjectCards.size)
        assertEquals(Subject.entries.toSet(), mappedSubjects)
    }

    @Test
    fun `GetQuestionBankQuestionsUseCase supports empty session for All BCS querying`() = runBlocking {
        val repo = FakeQuestionBankRepository()
        val useCase = GetQuestionBankQuestionsUseCase(repo)

        val result = useCase(session = "", subject = Subject.BENGALI, page = 0, pageSize = 20)
        assertTrue(result is Result.Success)
        val page = (result as Result.Success).value
        assertEquals(1, page.items.size)
    }

    @Test
    fun `sessions selection prioritizes first session with totalQuestions greater than zero`() {
        val sessions = listOf(
            BcsSessionSummaryDto("50th BCS Preli", 0),
            BcsSessionSummaryDto("49th BCS(General) Preli", 0),
            BcsSessionSummaryDto("47th BCS Preliminary", 198),
        )
        val selected = sessions.firstOrNull { it.totalQuestions > 0 }?.sessionName
            ?: sessions.first().sessionName
        assertEquals("47th BCS Preliminary", selected)
    }
}
