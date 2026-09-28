package com.prostuti.server.db.tables

import org.jetbrains.exposed.sql.Table

object ExamAttemptQuestionsTable : Table("exam_attempt_questions") {
    val attemptId = reference("attempt_id", ExamAttemptsTable.id)
    val questionId = reference("question_id", QuestionsTable.id)
    val orderIndex = integer("order_index")

    override val primaryKey = PrimaryKey(attemptId, questionId)
}
