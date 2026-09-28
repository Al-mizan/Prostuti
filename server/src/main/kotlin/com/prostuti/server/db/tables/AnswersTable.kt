package com.prostuti.server.db.tables

import com.prostuti.core.model.Option
import com.prostuti.core.model.SessionType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object AnswersTable : Table("answers") {
    val id = uuid("id")
    val sessionType = enumerationByName("session_type", 50, SessionType::class)
    val sessionId = uuid("session_id")
    val questionId = reference("question_id", QuestionsTable.id)
    val selectedOption = enumerationByName("selected_option", 50, Option::class)
    val isCorrect = bool("is_correct")
    val answeredAt = timestamp("answered_at")

    override val primaryKey = PrimaryKey(id)
}
