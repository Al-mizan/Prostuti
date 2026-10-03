package com.prostuti.feature.questionbank

import com.prostuti.core.common.Result
import com.prostuti.core.model.BcsSessionSummaryDto
import com.prostuti.core.model.Option
import com.prostuti.core.model.Page
import com.prostuti.core.model.QuestionBankItemDto
import com.prostuti.core.model.Subject
import com.prostuti.feature.questionbank.domain.GetBcsSessionsUseCase
import com.prostuti.feature.questionbank.domain.GetQuestionBankQuestionsUseCase
import com.prostuti.feature.questionbank.domain.QuestionBankRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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

        override suspend fun getSessions(): Result<List<BcsSessionSummaryDto>> =
            Result.Success(sampleSessions)

        override suspend fun getQuestions(
            session: String,
            subject: Subject?,
            page: Int,
            pageSize: Int,
        ): Result<Page<QuestionBankItemDto>> =
            Result.Success(Page(sampleQuestions, page, pageSize, sampleQuestions.size))
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
}
