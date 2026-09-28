package com.prostuti.server.services

import com.prostuti.core.model.ExamAnswerSubmission
import com.prostuti.core.model.ExamQuestionDto
import com.prostuti.core.model.ExamQuestionResultDto
import com.prostuti.core.model.ExamResultDto
import com.prostuti.core.model.ExamSessionDto
import com.prostuti.core.model.LeaderboardEntryDto
import com.prostuti.core.model.QuestionType
import com.prostuti.core.model.SessionType
import com.prostuti.server.db.DatabaseFactory.dbQuery
import com.prostuti.server.db.tables.AnswersTable
import com.prostuti.server.db.tables.ExamAttemptQuestionsTable
import com.prostuti.server.db.tables.ExamAttemptsTable
import com.prostuti.server.db.tables.QuestionsTable
import com.prostuti.server.db.tables.UsersTable
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.util.UUID
import kotlin.math.roundToInt

class ExamService {

    /**
     * Starts a solo timed BCS mock test attempt.
     */
    suspend fun startSession(
        userId: String,
        examSession: String,
        questionCount: Int = 50,
        durationMinutes: Int = 30,
    ): ExamSessionDto {
        require(questionCount in 5..200) { "Question count must be between 5 and 200" }
        require(durationMinutes in 5..180) { "Duration must be between 5 and 180 minutes" }
        val userUuid = runCatching { UUID.fromString(userId) }.getOrNull()
            ?: throw IllegalArgumentException("Invalid user ID")

        return dbQuery {
            val query = QuestionsTable.selectAll()
                .where { QuestionsTable.type eq QuestionType.BANK }

            val candidateQuestions = if (examSession.isNotBlank() && !examSession.equals("ALL", ignoreCase = true)) {
                query.where { (QuestionsTable.type eq QuestionType.BANK) and (QuestionsTable.examSession eq examSession) }
                    .toList()
            } else {
                query.toList()
            }

            if (candidateQuestions.isEmpty()) {
                throw NoSuchElementException("No questions found for session: $examSession")
            }

            val selectedQuestions = candidateQuestions.shuffled().take(questionCount)
            val sessionId = UUID.randomUUID()
            val now = Clock.System.now()

            ExamAttemptsTable.insert {
                it[ExamAttemptsTable.id] = sessionId
                it[ExamAttemptsTable.userId] = userUuid
                it[ExamAttemptsTable.examSession] = examSession
                it[ExamAttemptsTable.startedAt] = now
                it[ExamAttemptsTable.finishedAt] = null
                it[ExamAttemptsTable.timeTakenSeconds] = null
                it[ExamAttemptsTable.score] = null
            }

            selectedQuestions.forEachIndexed { index, row ->
                ExamAttemptQuestionsTable.insert {
                    it[ExamAttemptQuestionsTable.attemptId] = sessionId
                    it[ExamAttemptQuestionsTable.questionId] = row[QuestionsTable.id]
                    it[ExamAttemptQuestionsTable.orderIndex] = index
                }
            }

            ExamSessionDto(
                id = sessionId.toString(),
                examSession = examSession,
                totalQuestions = selectedQuestions.size,
                durationMinutes = durationMinutes,
                questions = selectedQuestions.map { row ->
                    ExamQuestionDto(
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

    /**
     * Submits all answers in a batch, computes score with BCS negative marking,
     * updates ExamAttemptsTable and AnswersTable, and returns full question explanations.
     */
    suspend fun submitExam(
        userId: String,
        sessionId: UUID,
        answers: List<ExamAnswerSubmission>,
        timeTakenSeconds: Int,
    ): ExamResultDto {
        val userUuid = runCatching { UUID.fromString(userId) }.getOrNull()
            ?: throw IllegalArgumentException("Invalid user ID")

        return dbQuery {
            val attempt = ExamAttemptsTable.selectAll()
                .where {
                    (ExamAttemptsTable.id eq sessionId) and (ExamAttemptsTable.userId eq userUuid)
                }
                .firstOrNull() ?: throw NoSuchElementException("Exam session not found")

            if (attempt[ExamAttemptsTable.finishedAt] != null) {
                throw IllegalStateException("Exam session has already been submitted")
            }

            val examSessionName = attempt[ExamAttemptsTable.examSession]

            // Join exam_attempt_questions with questions table
            val questionRows = (ExamAttemptQuestionsTable innerJoin QuestionsTable)
                .selectAll()
                .where { ExamAttemptQuestionsTable.attemptId eq sessionId }
                .orderBy(ExamAttemptQuestionsTable.orderIndex, SortOrder.ASC)
                .toList()

            if (questionRows.isEmpty()) {
                throw IllegalStateException("No questions found for this exam session")
            }

            val answersMap = answers.associateBy { it.questionId }
            val now = Clock.System.now()

            val questionResults = questionRows.map { row ->
                val qId = row[QuestionsTable.id]
                val qIdStr = qId.toString()
                val userSubmission = answersMap[qIdStr]
                val selectedOption = userSubmission?.selectedOption
                val correctOption = row[QuestionsTable.correctOption]
                val isCorrect = (selectedOption != null && selectedOption == correctOption)

                if (selectedOption != null) {
                    AnswersTable.insert {
                        it[AnswersTable.id] = UUID.randomUUID()
                        it[AnswersTable.sessionType] = SessionType.EXAM
                        it[AnswersTable.sessionId] = sessionId
                        it[AnswersTable.questionId] = qId
                        it[AnswersTable.selectedOption] = selectedOption
                        it[AnswersTable.isCorrect] = isCorrect
                        it[AnswersTable.answeredAt] = now
                    }
                }

                ExamQuestionResultDto(
                    questionId = qIdStr,
                    subject = row[QuestionsTable.subject],
                    questionText = row[QuestionsTable.questionText],
                    optionA = row[QuestionsTable.optionA],
                    optionB = row[QuestionsTable.optionB],
                    optionC = row[QuestionsTable.optionC],
                    optionD = row[QuestionsTable.optionD],
                    selectedOption = selectedOption,
                    correctOption = correctOption,
                    isCorrect = isCorrect,
                    explanation = row[QuestionsTable.explanation],
                    topic = row[QuestionsTable.topic],
                )
            }

            val totalQuestions = questionResults.size
            val correctCount = questionResults.count { it.isCorrect }
            val incorrectCount = questionResults.count { it.selectedOption != null && !it.isCorrect }
            val skippedCount = questionResults.count { it.selectedOption == null }

            // BCS preliminary negative marking rule: +1.0 per correct, -0.5 per wrong
            val netScore = (correctCount.toDouble() - (incorrectCount * 0.5)).coerceAtLeast(0.0)
            val integerScore = netScore.roundToInt()

            ExamAttemptsTable.update({ ExamAttemptsTable.id eq sessionId }) {
                it[ExamAttemptsTable.finishedAt] = now
                it[ExamAttemptsTable.timeTakenSeconds] = timeTakenSeconds
                it[ExamAttemptsTable.score] = integerScore
            }

            ExamResultDto(
                sessionId = sessionId.toString(),
                examSession = examSessionName,
                totalQuestions = totalQuestions,
                correctCount = correctCount,
                incorrectCount = incorrectCount,
                skippedCount = skippedCount,
                score = netScore,
                timeTakenSeconds = timeTakenSeconds,
                questions = questionResults,
            )
        }
    }

    /**
     * Retrieves top leaderboard ranking for a given exam session,
     * ordered by score DESC, timeTakenSeconds ASC.
     */
    suspend fun getLeaderboard(examSession: String): List<LeaderboardEntryDto> = dbQuery {
        val query = (ExamAttemptsTable innerJoin UsersTable)
            .selectAll()
            .where { ExamAttemptsTable.finishedAt.isNotNull() }

        val filteredQuery = if (examSession.isNotBlank() && !examSession.equals("ALL", ignoreCase = true)) {
            query.where {
                (ExamAttemptsTable.finishedAt.isNotNull()) and (ExamAttemptsTable.examSession eq examSession)
            }
        } else {
            query
        }

        val rows = filteredQuery
            .orderBy(ExamAttemptsTable.score, SortOrder.DESC)
            .orderBy(ExamAttemptsTable.timeTakenSeconds, SortOrder.ASC)
            .limit(50)
            .toList()

        rows.mapIndexed { index, row ->
            val scoreVal = row[ExamAttemptsTable.score]?.toDouble() ?: 0.0
            val timeTaken = row[ExamAttemptsTable.timeTakenSeconds] ?: 0
            val finishedAt = row[ExamAttemptsTable.finishedAt]?.toString().orEmpty()

            LeaderboardEntryDto(
                rank = index + 1,
                userId = row[UsersTable.id].toString(),
                userName = row[UsersTable.name],
                avatarId = null,
                score = scoreVal,
                timeTakenSeconds = timeTaken,
                finishedAt = finishedAt,
            )
        }
    }
}
