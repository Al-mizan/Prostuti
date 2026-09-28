package com.prostuti.server.services

import com.prostuti.core.model.SessionType
import com.prostuti.core.model.Subject
import com.prostuti.core.model.UserAttemptSummaryDto
import com.prostuti.core.model.WrongAnswerItemDto
import com.prostuti.server.db.DatabaseFactory.dbQuery
import com.prostuti.server.db.tables.AnswersTable
import com.prostuti.server.db.tables.ExamAttemptQuestionsTable
import com.prostuti.server.db.tables.ExamAttemptsTable
import com.prostuti.server.db.tables.PracticeSessionQuestionsTable
import com.prostuti.server.db.tables.PracticeSessionsTable
import com.prostuti.server.db.tables.QuestionsTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

class HistoryService {

    /**
     * Retrieves all completed practice and exam attempts for the user,
     * sorted descending by startedAt.
     */
    suspend fun getAttempts(userId: String): List<UserAttemptSummaryDto> {
        val userUuid = runCatching { UUID.fromString(userId) }.getOrNull()
            ?: throw IllegalArgumentException("Invalid user ID")

        return dbQuery {
            // 1. Practice sessions
            val practiceRows = PracticeSessionsTable.selectAll()
                .where {
                    (PracticeSessionsTable.userId eq userUuid) and PracticeSessionsTable.finishedAt.isNotNull()
                }
                .toList()

            val practiceSessionIds = practiceRows.map { it[PracticeSessionsTable.id] }
            val practiceCounts = if (practiceSessionIds.isNotEmpty()) {
                val countCol = PracticeSessionQuestionsTable.questionId.count()
                PracticeSessionQuestionsTable
                    .select(PracticeSessionQuestionsTable.sessionId, countCol)
                    .where { PracticeSessionQuestionsTable.sessionId inList practiceSessionIds }
                    .groupBy(PracticeSessionQuestionsTable.sessionId)
                    .associate { it[PracticeSessionQuestionsTable.sessionId] to it[countCol].toInt() }
            } else emptyMap()

            val practiceDtos = practiceRows.map { row ->
                val sId = row[PracticeSessionsTable.id]
                val subj = row[PracticeSessionsTable.subject]
                UserAttemptSummaryDto(
                    id = sId.toString(),
                    sessionType = SessionType.PRACTICE,
                    title = subj.toBanglaName(),
                    subject = subj,
                    examSession = null,
                    score = row[PracticeSessionsTable.score] ?: 0,
                    totalQuestions = practiceCounts[sId] ?: 0,
                    timeTakenSeconds = null,
                    startedAt = row[PracticeSessionsTable.startedAt].toString(),
                    finishedAt = row[PracticeSessionsTable.finishedAt]?.toString(),
                )
            }

            // 2. Exam attempts
            val examRows = ExamAttemptsTable.selectAll()
                .where {
                    (ExamAttemptsTable.userId eq userUuid) and ExamAttemptsTable.finishedAt.isNotNull()
                }
                .toList()

            val examAttemptIds = examRows.map { it[ExamAttemptsTable.id] }
            val examCounts = if (examAttemptIds.isNotEmpty()) {
                val countCol = ExamAttemptQuestionsTable.questionId.count()
                ExamAttemptQuestionsTable
                    .select(ExamAttemptQuestionsTable.attemptId, countCol)
                    .where { ExamAttemptQuestionsTable.attemptId inList examAttemptIds }
                    .groupBy(ExamAttemptQuestionsTable.attemptId)
                    .associate { it[ExamAttemptQuestionsTable.attemptId] to it[countCol].toInt() }
            } else emptyMap()

            val examDtos = examRows.map { row ->
                val aId = row[ExamAttemptsTable.id]
                UserAttemptSummaryDto(
                    id = aId.toString(),
                    sessionType = SessionType.EXAM,
                    title = row[ExamAttemptsTable.examSession],
                    subject = null,
                    examSession = row[ExamAttemptsTable.examSession],
                    score = row[ExamAttemptsTable.score] ?: 0,
                    totalQuestions = examCounts[aId] ?: 0,
                    timeTakenSeconds = row[ExamAttemptsTable.timeTakenSeconds],
                    startedAt = row[ExamAttemptsTable.startedAt].toString(),
                    finishedAt = row[ExamAttemptsTable.finishedAt]?.toString(),
                )
            }

            // Combine and sort chronologically descending
            (practiceDtos + examDtos).sortedByDescending { it.startedAt }
        }
    }

    /**
     * Derived wrong answers query:
     * SELECT answers JOIN questions WHERE is_correct = false AND session_id IN (user's sessions).
     */
    suspend fun getWrongAnswers(userId: String, subject: Subject? = null): List<WrongAnswerItemDto> {
        val userUuid = runCatching { UUID.fromString(userId) }.getOrNull()
            ?: throw IllegalArgumentException("Invalid user ID")

        return dbQuery {
            val userPracticeIds = PracticeSessionsTable.select(PracticeSessionsTable.id)
                .where { PracticeSessionsTable.userId eq userUuid }
                .map { it[PracticeSessionsTable.id] }

            val userExamIds = ExamAttemptsTable.select(ExamAttemptsTable.id)
                .where { ExamAttemptsTable.userId eq userUuid }
                .map { it[ExamAttemptsTable.id] }

            val allSessionIds = userPracticeIds + userExamIds
            if (allSessionIds.isEmpty()) return@dbQuery emptyList()

            var query = (AnswersTable innerJoin QuestionsTable)
                .selectAll()
                .where {
                    (AnswersTable.isCorrect eq false) and (AnswersTable.sessionId inList allSessionIds)
                }

            if (subject != null) {
                query = query.andWhere { QuestionsTable.subject eq subject }
            }

            query.orderBy(AnswersTable.answeredAt, SortOrder.DESC)
                .limit(100)
                .map { row ->
                    WrongAnswerItemDto(
                        answerId = row[AnswersTable.id].toString(),
                        sessionId = row[AnswersTable.sessionId].toString(),
                        sessionType = row[AnswersTable.sessionType],
                        questionId = row[QuestionsTable.id].toString(),
                        subject = row[QuestionsTable.subject],
                        topic = row[QuestionsTable.topic],
                        questionText = row[QuestionsTable.questionText],
                        optionA = row[QuestionsTable.optionA],
                        optionB = row[QuestionsTable.optionB],
                        optionC = row[QuestionsTable.optionC],
                        optionD = row[QuestionsTable.optionD],
                        selectedOption = row[AnswersTable.selectedOption],
                        correctOption = row[QuestionsTable.correctOption],
                        explanation = row[QuestionsTable.explanation],
                        answeredAt = row[AnswersTable.answeredAt].toString(),
                    )
                }
        }
    }

    private fun Subject.toBanglaName(): String = when (this) {
        Subject.BENGALI -> "বাংলা ভাষা ও সাহিত্য"
        Subject.ENGLISH -> "ইংরেজি ভাষা ও সাহিত্য"
        Subject.BD_INTERNATIONAL_AFFAIRS -> "বাংলাদেশ ও আন্তর্জাতিক বিষয়াবলি"
        Subject.GEOGRAPHY -> "ভূগোল, পরিবেশ ও দুর্যোগ"
        Subject.SCIENCE -> "সাধারণ বিজ্ঞান"
        Subject.IT -> "কম্পিউটার ও তথ্যপ্রযুক্তি"
        Subject.MATH -> "গাণিতিক যুক্তি"
        Subject.MENTAL_ABILITY -> "মানসিক দক্ষতা"
        Subject.ETHICS -> "নৈতিকতা, মূল্যবোধ ও সুশাসন"
    }
}
