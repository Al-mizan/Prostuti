package com.prostuti.server.services

import com.prostuti.core.model.AdminQuestionDto
import com.prostuti.core.model.AdminUserDto
import com.prostuti.core.model.ImportSummary
import com.prostuti.core.model.Option
import com.prostuti.core.model.QuestionType
import com.prostuti.core.model.Role
import com.prostuti.core.model.UpdateQuestionRequest
import com.prostuti.server.csv.CsvImporter.ParsedRow
import com.prostuti.server.db.DatabaseFactory.dbQuery
import com.prostuti.server.db.tables.ExamAttemptQuestionsTable
import com.prostuti.server.db.tables.PracticeSessionQuestionsTable
import com.prostuti.server.db.tables.QuestionsTable
import com.prostuti.server.db.tables.UsersTable
import com.prostuti.core.model.Page
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.insert
import java.util.UUID

/**
 * Admin operations: CSV import + question review + user role management.
 *
 * Routes call into this; this never touches HTTP. Errors throw
 * IllegalArgumentException (-> 400) or IllegalStateException (-> 409)
 * which Application.kt's StatusPages translates to JSON.
 */
class AdminService {

    // ─── CSV import ────────────────────────────────────────────────────

    suspend fun importQuestionBank(userId: String, rows: List<ParsedRow.Bank>): ImportSummary =
        insertAll(userId = userId, type = QuestionType.BANK, rows = rows.map { row ->
            buildInsertMap(
                examSession = row.examSession,
                subject = row.subject,
                topic = row.topic,
                questionText = row.questionText,
                optionA = row.optionA, optionB = row.optionB,
                optionC = row.optionC, optionD = row.optionD,
                correctOption = row.correctOption,
                explanation = row.explanation,
                difficulty = row.difficulty,
            )
        })

    suspend fun importPractice(userId: String, rows: List<ParsedRow.Practice>): ImportSummary =
        insertAll(userId = userId, type = QuestionType.PRACTICE, rows = rows.map { row ->
            buildInsertMap(
                examSession = null,
                subject = row.subject,
                topic = row.topic,
                questionText = row.questionText,
                optionA = row.optionA, optionB = row.optionB,
                optionC = row.optionC, optionD = row.optionD,
                correctOption = row.correctOption,
                explanation = row.explanation,
                difficulty = row.difficulty,
            )
        })

    private suspend fun insertAll(
        userId: String,
        type: QuestionType,
        rows: List<Map<String, Any?>>,
    ): ImportSummary {
        if (rows.isEmpty()) return ImportSummary(imported = 0, rejected = emptyList())

        val creatorUuid = UUID.fromString(userId)
        dbQuery {
            rows.forEach { values ->
                QuestionsTable.insert {
                    it[QuestionsTable.id] = UUID.randomUUID()
                    it[QuestionsTable.type] = type
                    it[QuestionsTable.examSession] = values["exam_session"] as String?
                    it[QuestionsTable.subject] = values["subject"] as com.prostuti.core.model.Subject
                    it[QuestionsTable.topic] = values["topic"] as String?
                    it[QuestionsTable.questionText] = values["question_text"] as String
                    it[QuestionsTable.optionA] = values["option_a"] as String
                    it[QuestionsTable.optionB] = values["option_b"] as String
                    it[QuestionsTable.optionC] = values["option_c"] as String
                    it[QuestionsTable.optionD] = values["option_d"] as String
                    it[QuestionsTable.correctOption] = values["correct_option"] as Option
                    it[QuestionsTable.explanation] = values["explanation"] as String?
                    it[QuestionsTable.difficulty] = values["difficulty"] as com.prostuti.core.model.Difficulty?
                    it[QuestionsTable.createdBy] = creatorUuid
                    it[QuestionsTable.createdAt] = Clock.System.now()
                }
            }
        }
        // ImportSummary already collected per-row rejections in CsvImporter;
        // the service-level call only sees the valid subset.
        return ImportSummary(imported = rows.size, rejected = emptyList())
    }

    @Suppress("UNCHECKED_CAST")
    private fun buildInsertMap(
        examSession: String?,
        subject: com.prostuti.core.model.Subject,
        topic: String?,
        questionText: String,
        optionA: String, optionB: String, optionC: String, optionD: String,
        correctOption: Option,
        explanation: String?,
        difficulty: com.prostuti.core.model.Difficulty?,
    ): Map<String, Any?> = mapOf(
        "exam_session" to examSession,
        "subject" to subject,
        "topic" to topic,
        "question_text" to questionText,
        "option_a" to optionA,
        "option_b" to optionB,
        "option_c" to optionC,
        "option_d" to optionD,
        "correct_option" to correctOption,
        "explanation" to explanation,
        "difficulty" to difficulty,
    )

    // ─── Question review ───────────────────────────────────────────────

    /**
     * Composable filter: any combination of type/examSession/subject is allowed.
     * Pagination is offset-based — fine for a course project, not a deep
     * leaderboard scrape.
     */
    suspend fun listQuestions(
        type: QuestionType?,
        examSession: String?,
        subject: com.prostuti.core.model.Subject?,
        page: Int,
        pageSize: Int,
    ): Page<AdminQuestionDto> {
        require(page >= 0) { "page must be >= 0" }
        require(pageSize in 1..200) { "pageSize must be in 1..200" }

        return dbQuery {
            var query = QuestionsTable.selectAll()
            if (type != null) {
                query = query.andWhere { QuestionsTable.type eq type }
            }
            if (examSession != null) {
                query = query.andWhere { QuestionsTable.examSession eq examSession }
            }
            if (subject != null) {
                query = query.andWhere { QuestionsTable.subject eq subject }
            }

            val total = query.count()
            val items = query
                .orderBy(QuestionsTable.createdAt, SortOrder.DESC)
                .limit(pageSize, offset = (page * pageSize).toLong())
                .map { it.toAdminQuestionDto() }

            Page(items = items, page = page, pageSize = pageSize, total = total.toInt())
        }
    }

    suspend fun updateQuestion(id: UUID, req: UpdateQuestionRequest): AdminQuestionDto = dbQuery {
        val existing = QuestionsTable.selectAll().where { QuestionsTable.id eq id }.firstOrNull()
            ?: throw IllegalArgumentException("Question $id not found")

        // BANK rows MUST keep an exam_session; we don't allow editing
        // examSession via the request anyway (it's not in UpdateQuestionRequest)
        // but we enforce it on update so a stray DB edit can't blank it.
        val type = existing[QuestionsTable.type]
        val examSession = existing[QuestionsTable.examSession]
        if (type == QuestionType.BANK && examSession.isNullOrBlank()) {
            throw IllegalStateException("BANK question is missing exam_session — fix by re-importing")
        }

        QuestionsTable.update({ QuestionsTable.id eq id }) {
            it[QuestionsTable.subject] = req.subject
            it[QuestionsTable.topic] = req.topic
            it[QuestionsTable.questionText] = req.questionText
            it[QuestionsTable.optionA] = req.optionA
            it[QuestionsTable.optionB] = req.optionB
            it[QuestionsTable.optionC] = req.optionC
            it[QuestionsTable.optionD] = req.optionD
            it[QuestionsTable.correctOption] = req.correctOption
            it[QuestionsTable.explanation] = req.explanation
            it[QuestionsTable.difficulty] = req.difficulty
        }

        QuestionsTable.selectAll().where { QuestionsTable.id eq id }.single().toAdminQuestionDto()
    }

    /**
     * Refuses to delete a question that is referenced by any session
     * attempt row — orphaning answers would break history/wrong-answers
     * derived queries. Soft-delete would be the right move in production;
     * for the course scope, hard-delete + a clear error message is fine.
     */
    suspend fun deleteQuestion(id: UUID) {
        dbQuery {
            val practiceRef = PracticeSessionQuestionsTable
                .selectAll().where { PracticeSessionQuestionsTable.questionId eq id }
                .limit(1).firstOrNull()
            val examRef = ExamAttemptQuestionsTable
                .selectAll().where { ExamAttemptQuestionsTable.questionId eq id }
                .limit(1).firstOrNull()
            if (practiceRef != null || examRef != null) {
                throw IllegalStateException(
                    "Question is referenced by a saved session and cannot be deleted"
                )
            }

            val rows = QuestionsTable.deleteWhere { QuestionsTable.id eq id }
            if (rows == 0) throw IllegalArgumentException("Question $id not found")
        }
    }

    // ─── User management ───────────────────────────────────────────────

    suspend fun listUsers(): List<AdminUserDto> = dbQuery {
        UsersTable.selectAll()
            .orderBy(UsersTable.createdAt, SortOrder.DESC)
            .map { it.toAdminUserDto() }
    }

    /**
     * Demoting a user to STUDENT is refused if it would leave the system
     * with zero admins — locking everyone out is a real foot-gun for the
     * dev team.
     */
    suspend fun updateUserRole(userId: UUID, newRole: Role): AdminUserDto = dbQuery {
        val existing = UsersTable.selectAll().where { UsersTable.id eq userId }.firstOrNull()
            ?: throw IllegalArgumentException("User $userId not found")

        if (existing[UsersTable.role] == Role.ADMIN && newRole == Role.STUDENT) {
            val adminCount = UsersTable.selectAll()
                .where { UsersTable.role eq Role.ADMIN }
                .count()
            if (adminCount <= 1) {
                throw IllegalStateException(
                    "Cannot demote the last remaining admin"
                )
            }
        }

        UsersTable.update({ UsersTable.id eq userId }) {
            it[UsersTable.role] = newRole
        }

        UsersTable.selectAll().where { UsersTable.id eq userId }.single().toAdminUserDto()
    }

    // ─── Mapping helpers ───────────────────────────────────────────────

    private fun ResultRow.toAdminQuestionDto(): AdminQuestionDto {
        val createdAt = this[QuestionsTable.createdAt]
        return AdminQuestionDto(
            id = this[QuestionsTable.id].toString(),
            type = this[QuestionsTable.type],
            subject = this[QuestionsTable.subject],
            examSession = this[QuestionsTable.examSession],
            topic = this[QuestionsTable.topic],
            questionText = this[QuestionsTable.questionText],
            optionA = this[QuestionsTable.optionA],
            optionB = this[QuestionsTable.optionB],
            optionC = this[QuestionsTable.optionC],
            optionD = this[QuestionsTable.optionD],
            correctOption = this[QuestionsTable.correctOption],
            explanation = this[QuestionsTable.explanation],
            difficulty = this[QuestionsTable.difficulty],
            createdAt = createdAt.toString(),
        )
    }

    private fun ResultRow.toAdminUserDto(): AdminUserDto {
        val createdAt = this[UsersTable.createdAt]
        return AdminUserDto(
            id = this[UsersTable.id].toString(),
            name = this[UsersTable.name],
            email = this[UsersTable.email],
            role = this[UsersTable.role],
            createdAt = createdAt.toString(),
        )
    }
}