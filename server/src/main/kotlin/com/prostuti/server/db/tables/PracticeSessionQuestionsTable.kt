package com.prostuti.server.db.tables

import org.jetbrains.exposed.sql.Table

object PracticeSessionQuestionsTable : Table("practice_session_questions") {
    val sessionId = reference("session_id", PracticeSessionsTable.id)
    val questionId = reference("question_id", QuestionsTable.id)
    val orderIndex = integer("order_index")

    override val primaryKey = PrimaryKey(sessionId, questionId)
}
