package com.prostuti.server.services

import com.prostuti.core.model.FinishPracticeSessionResponse
import com.prostuti.core.model.Option
import com.prostuti.core.model.PracticeAnswerResultDto
import com.prostuti.core.model.PracticeSessionDto
import com.prostuti.core.model.PracticeSessionQuestionDto
import com.prostuti.core.model.SessionType
import com.prostuti.core.model.Subject
import com.prostuti.server.db.DatabaseFactory.dbQuery
import com.prostuti.server.db.tables.AnswersTable
import com.prostuti.server.db.tables.PracticeSessionQuestionsTable
import com.prostuti.server.db.tables.PracticeSessionsTable
import com.prostuti.server.db.tables.QuestionsTable
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.util.UUID

class PracticeService {

    suspend fun startSession(
        userId: String,
        subject: Subject,
        count: Int = 10,
    ): PracticeSessionDto {
        require(count in 5..50) { "Question count must be between 5 and 50" }
        val userUuid = runCatching { UUID.fromString(userId) }.getOrNull()
            ?: throw IllegalArgumentException("Invalid user ID")

        return dbQuery {
            // Fetch questions for this subject from pool
            val candidateQuestions = QuestionsTable.selectAll()
                .where { QuestionsTable.subject eq subject }
                .limit(count * 4)
                .toList()

            if (candidateQuestions.isEmpty()) {
                throw NoSuchElementException("No questions available for subject: $subject")
            }

            val selectedQuestions = candidateQuestions.shuffled().take(count)
            val sessionId = UUID.randomUUID()
            val now = Clock.System.now()

            PracticeSessionsTable.insert {
                it[PracticeSessionsTable.id] = sessionId
                it[PracticeSessionsTable.userId] = userUuid
                it[PracticeSessionsTable.subject] = subject
                it[PracticeSessionsTable.startedAt] = now
                it[PracticeSessionsTable.finishedAt] = null
                it[PracticeSessionsTable.score] = null
            }

            selectedQuestions.forEachIndexed { index, row ->
                PracticeSessionQuestionsTable.insert {
                    it[PracticeSessionQuestionsTable.sessionId] = sessionId
                    it[PracticeSessionQuestionsTable.questionId] = row[QuestionsTable.id]
                    it[PracticeSessionQuestionsTable.orderIndex] = index
                }
            }

            PracticeSessionDto(
                id = sessionId.toString(),
                subject = subject,
                questions = selectedQuestions.map { row ->
                    PracticeSessionQuestionDto(
                        id = row[QuestionsTable.id].toString(),
                        subject = row[QuestionsTable.subject],
                        questionText = row[QuestionsTable.questionText],
                        optionA = row[QuestionsTable.optionA],
                        optionB = row[QuestionsTable.optionB],
                        optionC = row[QuestionsTable.optionC],
                        optionD = row[QuestionsTable.optionD],
                        topic = row[QuestionsTable.topic],
                        difficulty = row[QuestionsTable.difficulty],
                    )
                },
                startedAt = now.toString(),
            )
        }
    }

    suspend fun submitAnswer(
        userId: String,
        sessionId: UUID,
        questionId: UUID,
        selectedOption: Option,
    ): PracticeAnswerResultDto {
        val userUuid = runCatching { UUID.fromString(userId) }.getOrNull()
            ?: throw IllegalArgumentException("Invalid user ID")

        return dbQuery {
            val session = PracticeSessionsTable.selectAll()
                .where {
                    (PracticeSessionsTable.id eq sessionId) and (PracticeSessionsTable.userId eq userUuid)
                }
                .firstOrNull() ?: throw NoSuchElementException("Practice session not found")

            if (session[PracticeSessionsTable.finishedAt] != null) {
                throw IllegalStateException("Practice session is already finished")
            }

            val question = QuestionsTable.selectAll()
                .where { QuestionsTable.id eq questionId }
                .firstOrNull() ?: throw NoSuchElementException("Question not found")

            val correctOption = question[QuestionsTable.correctOption]
            val explanation = question[QuestionsTable.explanation]
            val isCorrect = (selectedOption == correctOption)

            AnswersTable.insert {
                it[AnswersTable.id] = UUID.randomUUID()
                it[AnswersTable.sessionType] = SessionType.PRACTICE
                it[AnswersTable.sessionId] = sessionId
                it[AnswersTable.questionId] = questionId
                it[AnswersTable.selectedOption] = selectedOption
                it[AnswersTable.isCorrect] = isCorrect
                it[AnswersTable.answeredAt] = Clock.System.now()
            }

            PracticeAnswerResultDto(
                questionId = questionId.toString(),
                selectedOption = selectedOption,
                isCorrect = isCorrect,
                correctOption = correctOption,
                explanation = explanation,
            )
        }
    }

    suspend fun finishSession(
        userId: String,
        sessionId: UUID,
    ): FinishPracticeSessionResponse {
        val userUuid = runCatching { UUID.fromString(userId) }.getOrNull()
            ?: throw IllegalArgumentException("Invalid user ID")

        return dbQuery {
            val session = PracticeSessionsTable.selectAll()
                .where {
                    (PracticeSessionsTable.id eq sessionId) and (PracticeSessionsTable.userId eq userUuid)
                }
                .firstOrNull() ?: throw NoSuchElementException("Practice session not found")

            val totalQuestions = PracticeSessionQuestionsTable.selectAll()
                .where { PracticeSessionQuestionsTable.sessionId eq sessionId }
                .count().toInt()

            val answers = AnswersTable.selectAll()
                .where {
                    (AnswersTable.sessionId eq sessionId) and (AnswersTable.sessionType eq SessionType.PRACTICE)
                }
                .toList()

            val correctCount = answers.count { it[AnswersTable.isCorrect] }
            val incorrectCount = answers.count { !it[AnswersTable.isCorrect] }
            val score = correctCount
            val now = Clock.System.now()

            PracticeSessionsTable.update({ PracticeSessionsTable.id eq sessionId }) {
                it[PracticeSessionsTable.score] = score
                it[PracticeSessionsTable.finishedAt] = now
            }

            FinishPracticeSessionResponse(
                sessionId = sessionId.toString(),
                subject = session[PracticeSessionsTable.subject],
                totalQuestions = totalQuestions,
                correctCount = correctCount,
                incorrectCount = incorrectCount,
                score = score,
            )
        }
    }
}
