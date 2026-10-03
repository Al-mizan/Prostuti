package com.prostuti.server.services

import com.prostuti.core.model.BcsSessionSummaryDto
import com.prostuti.core.model.Page
import com.prostuti.core.model.QuestionBankItemDto
import com.prostuti.core.model.QuestionType
import com.prostuti.core.model.Subject
import com.prostuti.server.db.DatabaseFactory.dbQuery
import com.prostuti.server.db.tables.QuestionsTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.selectAll

class QuestionBankService {

    /**
     * Lists all distinct BCS Preliminary exam sessions with their total question counts.
     * Sorted descending by BCS edition number (e.g., 47th BCS, 46th BCS down to 10th BCS).
     */
    suspend fun listSessions(): List<BcsSessionSummaryDto> = dbQuery {
        val countColumn = QuestionsTable.id.count()
        val dbCounts = QuestionsTable
            .select(QuestionsTable.examSession, countColumn)
            .where {
                (QuestionsTable.type eq QuestionType.BANK) and QuestionsTable.examSession.isNotNull()
            }
            .groupBy(QuestionsTable.examSession)
            .associate { row ->
                val session = row[QuestionsTable.examSession] ?: ""
                session to row[countColumn].toInt()
            }

        val defaultArchive = (50 downTo 10).map { edition ->
            val isShort = edition in setOf(49, 42, 33) || edition <= 34
            val sessionName = when (edition) {
                49 -> "49th BCS(General) Preli"
                48 -> "48th BCS(Special) Preli"
                else -> "${edition}th BCS Preli"
            }
            val defaultCount = if (edition == 37) 198 else if (isShort) 100 else 200
            val duration = if (isShort) 60 else 120
            val marks = if (isShort) 100.0 else 200.0

            val count = dbCounts.entries.firstOrNull { it.key.contains("${edition}th") || it.key == sessionName }?.value
                ?: defaultCount

            BcsSessionSummaryDto(
                sessionName = sessionName,
                totalQuestions = count,
                durationMinutes = duration,
                totalMarks = marks,
                negativeMarkingPerQuestion = 0.5,
            )
        }

        val customDbSessions = dbCounts.filterKeys { dbName ->
            defaultArchive.none { it.sessionName == dbName || dbName.contains(Regex("""\b\d+th\b""")) }
        }.map { (name, count) ->
            BcsSessionSummaryDto(
                sessionName = name,
                totalQuestions = count,
                durationMinutes = 120,
                totalMarks = 200.0,
                negativeMarkingPerQuestion = 0.5,
            )
        }

        (customDbSessions + defaultArchive).sortedWith(
            compareByDescending<BcsSessionSummaryDto> { dto ->
                Regex("""\d+""").find(dto.sessionName)?.value?.toIntOrNull() ?: 0
            }.thenByDescending { it.sessionName }
        )
    }

    /**
     * Lists questions for an exam session, optionally filtered by subject.
     * Paginated with page and pageSize.
     */
    suspend fun listQuestions(
        examSession: String,
        subject: Subject?,
        page: Int,
        pageSize: Int,
    ): Page<QuestionBankItemDto> {
        require(page >= 0) { "page must be >= 0" }
        require(pageSize in 1..200) { "pageSize must be in 1..200" }

        return dbQuery {
            var query = QuestionsTable.selectAll()
                .where {
                    (QuestionsTable.type eq QuestionType.BANK) and (QuestionsTable.examSession eq examSession)
                }

            if (subject != null) {
                query = query.andWhere { QuestionsTable.subject eq subject }
            }

            val total = query.count().toInt()
            val items = query
                .orderBy(QuestionsTable.createdAt, SortOrder.ASC)
                .limit(pageSize, offset = (page * pageSize).toLong())
                .map { it.toQuestionBankItemDto() }

            Page(
                items = items,
                page = page,
                pageSize = pageSize,
                total = total,
            )
        }
    }

    private fun ResultRow.toQuestionBankItemDto() = QuestionBankItemDto(
        id = this[QuestionsTable.id].toString(),
        subject = this[QuestionsTable.subject],
        examSession = this[QuestionsTable.examSession].orEmpty(),
        topic = this[QuestionsTable.topic],
        questionText = this[QuestionsTable.questionText],
        optionA = this[QuestionsTable.optionA],
        optionB = this[QuestionsTable.optionB],
        optionC = this[QuestionsTable.optionC],
        optionD = this[QuestionsTable.optionD],
        correctOption = this[QuestionsTable.correctOption],
        explanation = this[QuestionsTable.explanation],
        difficulty = this[QuestionsTable.difficulty],
    )
}
