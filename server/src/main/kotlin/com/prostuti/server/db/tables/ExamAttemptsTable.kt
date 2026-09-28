package com.prostuti.server.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ExamAttemptsTable : Table("exam_attempts") {
    val id = uuid("id")
    val userId = reference("user_id", UsersTable.id)
    val examSession = text("exam_session")
    val startedAt = timestamp("started_at")
    val finishedAt = timestamp("finished_at").nullable()
    val timeTakenSeconds = integer("time_taken_seconds").nullable()
    val score = integer("score").nullable()

    override val primaryKey = PrimaryKey(id)
}
