package com.prostuti.server.db.tables

import com.prostuti.core.model.Difficulty
import com.prostuti.core.model.Option
import com.prostuti.core.model.QuestionType
import com.prostuti.core.model.Subject
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object QuestionsTable : Table("questions") {
    val id = uuid("id")
    val type = enumerationByName("type", 50, QuestionType::class)
    val subject = enumerationByName("subject", 50, Subject::class)
    val examSession = text("exam_session").nullable()
    val topic = text("topic").nullable()
    val questionText = text("question_text")
    val optionA = text("option_a")
    val optionB = text("option_b")
    val optionC = text("option_c")
    val optionD = text("option_d")
    val correctOption = enumerationByName("correct_option", 50, Option::class)
    val explanation = text("explanation").nullable()
    val difficulty = enumerationByName("difficulty", 50, Difficulty::class).nullable()
    val createdBy = reference("created_by", UsersTable.id)
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}
