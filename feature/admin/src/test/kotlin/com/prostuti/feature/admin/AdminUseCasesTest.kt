package com.prostuti.feature.admin

import com.prostuti.core.common.Result
import com.prostuti.core.model.*
import com.prostuti.feature.admin.domain.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class AdminUseCasesTest {

    private class FakeAdminRepository : AdminRepository {
        var importBankCalled = false
        var importPracticeCalled = false
        var deleteCalledId: String? = null

        val sampleQuestions = mutableListOf(
            AdminQuestionDto(
                id = "q-1",
                type = QuestionType.BANK,
                subject = Subject.BENGALI,
                examSession = "45th BCS Preliminary",
                topic = "সন্ধি",
                questionText = "'গায়ক' এর সন্ধি বিচ্ছেদ কোনটি?",
                optionA = "গা + য়ক",
                optionB = "গৈ + অক",
                optionC = "গো + অক",
                optionD = "গা + অক",
                correctOption = Option.B,
                explanation = "গৈ + অক = গায়ক",
                difficulty = Difficulty.MEDIUM,
                createdAt = "2026-09-29T00:00:00Z",
            ),
            AdminQuestionDto(
                id = "q-2",
                type = QuestionType.PRACTICE,
                subject = Subject.MATH,
                examSession = null,
                topic = "বীজগণিত",
                questionText = "x + y = 5 এবং x - y = 3 হলে xy = কত?",
                optionA = "2",
                optionB = "3",
                optionC = "4",
                optionD = "5",
                correctOption = Option.C,
                explanation = "xy = ((x+y)/2)^2 - ((x-y)/2)^2 = 4",
                difficulty = Difficulty.EASY,
                createdAt = "2026-09-29T01:00:00Z",
            )
        )

        val sampleUsers = mutableListOf(
            AdminUserDto(
                id = "u-admin",
                name = "Admin User",
                email = "admin@prostuti.com",
                role = Role.ADMIN,
                createdAt = "2026-09-20T00:00:00Z",
            ),
            AdminUserDto(
                id = "u-student",
                name = "Student User",
                email = "student@prostuti.com",
                role = Role.STUDENT,
                createdAt = "2026-09-21T00:00:00Z",
            )
        )

        override suspend fun importQuestionBankCsv(fileName: String, bytes: ByteArray): Result<ImportSummary> {
            importBankCalled = true
            return Result.Success(ImportSummary(imported = 10, rejected = emptyList()))
        }

        override suspend fun importPracticeCsv(fileName: String, bytes: ByteArray): Result<ImportSummary> {
            importPracticeCalled = true
            return Result.Success(ImportSummary(imported = 5, rejected = emptyList()))
        }

        override suspend fun getQuestions(
            type: QuestionType?,
            examSession: String?,
            subject: Subject?,
            page: Int,
            pageSize: Int,
        ): Result<Page<AdminQuestionDto>> {
            var filtered = sampleQuestions.toList()
            if (type != null) filtered = filtered.filter { it.type == type }
            if (subject != null) filtered = filtered.filter { it.subject == subject }
            if (examSession != null) filtered = filtered.filter { it.examSession == examSession }
            return Result.Success(
                Page(items = filtered, page = page, pageSize = pageSize, total = filtered.size)
            )
        }

        override suspend fun updateQuestion(id: String, request: UpdateQuestionRequest): Result<AdminQuestionDto> {
            val idx = sampleQuestions.indexOfFirst { it.id == id }
            if (idx == -1) return Result.Error("Question not found")
            val current = sampleQuestions[idx]
            val updated = current.copy(
                subject = request.subject,
                topic = request.topic,
                questionText = request.questionText,
                optionA = request.optionA,
                optionB = request.optionB,
                optionC = request.optionC,
                optionD = request.optionD,
                correctOption = request.correctOption,
                explanation = request.explanation,
                difficulty = request.difficulty,
            )
            sampleQuestions[idx] = updated
            return Result.Success(updated)
        }

        override suspend fun deleteQuestion(id: String): Result<Unit> {
            deleteCalledId = id
            val removed = sampleQuestions.removeIf { it.id == id }
            return if (removed) Result.Success(Unit) else Result.Error("Not found")
        }

        val sampleModelTests = mutableListOf(
            ModelTestDto(
                id = "mt-1",
                title = "47th BCS Special Model Test",
                description = "Full length model test",
                examSession = "47th BCS Preliminary",
                durationMinutes = 120,
                totalMarks = 200.0,
                totalQuestions = 200,
                startTime = "2026-10-01T10:00:00Z",
                endTime = "2026-10-10T10:00:00Z",
                status = ModelTestStatus.LIVE,
                isPublished = true,
            )
        )

        override suspend fun getModelTests(): Result<List<ModelTestDto>> =
            Result.Success(sampleModelTests.toList())

        override suspend fun createModelTest(request: CreateModelTestRequest): Result<ModelTestDto> {
            val created = ModelTestDto(
                id = "mt-${sampleModelTests.size + 1}",
                title = request.title,
                description = request.description,
                examSession = request.examSession,
                durationMinutes = request.durationMinutes,
                totalMarks = request.totalMarks,
                totalQuestions = request.totalQuestions,
                startTime = request.startTime,
                endTime = request.endTime,
                status = ModelTestStatus.UPCOMING,
                isPublished = request.isPublished,
            )
            sampleModelTests.add(created)
            return Result.Success(created)
        }

        override suspend fun updateModelTest(id: String, request: UpdateModelTestRequest): Result<ModelTestDto> {
            val idx = sampleModelTests.indexOfFirst { it.id == id }
            if (idx == -1) return Result.Error("Model test not found")
            val current = sampleModelTests[idx]
            val updated = current.copy(
                title = request.title ?: current.title,
                description = request.description ?: current.description,
                examSession = request.examSession ?: current.examSession,
                durationMinutes = request.durationMinutes ?: current.durationMinutes,
                totalMarks = request.totalMarks ?: current.totalMarks,
                totalQuestions = request.totalQuestions ?: current.totalQuestions,
                startTime = request.startTime ?: current.startTime,
                endTime = request.endTime ?: current.endTime,
                isPublished = request.isPublished ?: current.isPublished,
            )
            sampleModelTests[idx] = updated
            return Result.Success(updated)
        }

        override suspend fun deleteModelTest(id: String): Result<Boolean> {
            val removed = sampleModelTests.removeIf { it.id == id }
            return Result.Success(removed)
        }

        override suspend fun getUsers(): Result<List<AdminUserDto>> =
            Result.Success(sampleUsers)

        override suspend fun updateUserRole(id: String, role: Role): Result<AdminUserDto> {
            val idx = sampleUsers.indexOfFirst { it.id == id }
            if (idx == -1) return Result.Error("User not found")
            val updated = sampleUsers[idx].copy(role = role)
            sampleUsers[idx] = updated
            return Result.Success(updated)
        }
    }

    @Test
    fun `ImportCsvUseCase invokes importQuestionBankCsv for BANK type`() = runBlocking {
        val repo = FakeAdminRepository()
        val useCase = ImportCsvUseCase(repo)

        val result = useCase(QuestionType.BANK, "bank.csv", byteArrayOf(1, 2, 3))
        assertTrue(result is Result.Success)
        assertTrue(repo.importBankCalled)
        assertFalse(repo.importPracticeCalled)
        assertEquals(10, (result as Result.Success).value.imported)
    }

    @Test
    fun `ImportCsvUseCase invokes importPracticeCsv for PRACTICE type`() = runBlocking {
        val repo = FakeAdminRepository()
        val useCase = ImportCsvUseCase(repo)

        val result = useCase(QuestionType.PRACTICE, "practice.csv", byteArrayOf(4, 5, 6))
        assertTrue(result is Result.Success)
        assertTrue(repo.importPracticeCalled)
        assertFalse(repo.importBankCalled)
        assertEquals(5, (result as Result.Success).value.imported)
    }

    @Test
    fun `GetAdminQuestionsUseCase filters by type and subject correctly`() = runBlocking {
        val repo = FakeAdminRepository()
        val useCase = GetAdminQuestionsUseCase(repo)

        val all = useCase()
        assertTrue(all is Result.Success)
        assertEquals(2, (all as Result.Success).value.items.size)

        val bankOnly = useCase(type = QuestionType.BANK)
        assertTrue(bankOnly is Result.Success)
        assertEquals(1, (bankOnly as Result.Success).value.items.size)
        assertEquals("q-1", (bankOnly as Result.Success).value.items[0].id)

        val mathOnly = useCase(subject = Subject.MATH)
        assertTrue(mathOnly is Result.Success)
        assertEquals(1, (mathOnly as Result.Success).value.items.size)
        assertEquals("q-2", (mathOnly as Result.Success).value.items[0].id)
    }

    @Test
    fun `UpdateAdminQuestionUseCase modifies existing question fields`() = runBlocking {
        val repo = FakeAdminRepository()
        val useCase = UpdateAdminQuestionUseCase(repo)

        val req = UpdateQuestionRequest(
            subject = Subject.BENGALI,
            topic = "সন্ধি",
            questionText = "গায়ক শব্দের সঠিক সন্ধি বিচ্ছেদ কোনটি?",
            optionA = "গা + য়ক",
            optionB = "গৈ + অক",
            optionC = "গো + অক",
            optionD = "গা + অক",
            correctOption = Option.B,
            explanation = "গৈ + অক = গায়ক (স্বরসন্ধির নিয়ম)",
            difficulty = Difficulty.HARD,
        )

        val result = useCase("q-1", req)
        assertTrue(result is Result.Success)
        val updated = (result as Result.Success).value
        assertEquals("গায়ক শব্দের সঠিক সন্ধি বিচ্ছেদ কোনটি?", updated.questionText)
        assertEquals(Difficulty.HARD, updated.difficulty)
    }

    @Test
    fun `DeleteAdminQuestionUseCase deletes question by ID`() = runBlocking {
        val repo = FakeAdminRepository()
        val useCase = DeleteAdminQuestionUseCase(repo)

        val result = useCase("q-1")
        assertTrue(result is Result.Success)
        assertEquals("q-1", repo.deleteCalledId)
        assertEquals(1, repo.sampleQuestions.size)
    }

    @Test
    fun `GetAdminUsersUseCase and UpdateUserRoleUseCase work as expected`() = runBlocking {
        val repo = FakeAdminRepository()
        val getUsers = GetAdminUsersUseCase(repo)
        val updateRole = UpdateUserRoleUseCase(repo)

        val usersRes = getUsers()
        assertTrue(usersRes is Result.Success)
        assertEquals(2, (usersRes as Result.Success).value.size)

        val promoteRes = updateRole("u-student", Role.ADMIN)
        assertTrue(promoteRes is Result.Success)
        assertEquals(Role.ADMIN, (promoteRes as Result.Success).value.role)
    }

    @Test
    fun `ModelTest use cases create, get, update, and delete correctly`() = runBlocking {
        val repo = FakeAdminRepository()
        val getTests = GetAdminModelTestsUseCase(repo)
        val createTest = CreateAdminModelTestUseCase(repo)
        val updateTest = UpdateAdminModelTestUseCase(repo)
        val deleteTest = DeleteAdminModelTestUseCase(repo)

        // Get initial
        val initial = getTests()
        assertTrue(initial is Result.Success)
        assertEquals(1, (initial as Result.Success).value.size)

        // Create new
        val createReq = CreateModelTestRequest(
            title = "48th BCS Model Test 1",
            description = "Special Preli Model Test",
            examSession = "48th BCS Preliminary",
            durationMinutes = 120,
            totalMarks = 200.0,
            totalQuestions = 200,
            startTime = "2026-11-01T10:00:00Z",
            endTime = "2026-11-10T10:00:00Z",
            isPublished = true,
        )
        val createdRes = createTest(createReq)
        assertTrue(createdRes is Result.Success)
        val created = (createdRes as Result.Success).value
        assertEquals("48th BCS Model Test 1", created.title)
        assertEquals("mt-2", created.id)

        // Verify size
        val afterCreate = getTests()
        assertTrue(afterCreate is Result.Success)
        assertEquals(2, (afterCreate as Result.Success).value.size)

        // Update
        val updateReq = UpdateModelTestRequest(
            title = "48th BCS Model Test 1 (Updated)",
        )
        val updatedRes = updateTest("mt-2", updateReq)
        assertTrue(updatedRes is Result.Success)
        assertEquals("48th BCS Model Test 1 (Updated)", (updatedRes as Result.Success).value.title)

        // Delete
        val deleteRes = deleteTest("mt-2")
        assertTrue(deleteRes is Result.Success)
        assertTrue((deleteRes as Result.Success).value)

        val afterDelete = getTests()
        assertTrue(afterDelete is Result.Success)
        assertEquals(1, (afterDelete as Result.Success).value.size)
    }
}
