package com.prostuti.server.db.tables

import com.prostuti.core.model.Subject
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object PracticeSessionsTable : Table("practice_sessions") {
    val id = uuid("id")
    val userId = reference("user_id", UsersTable.id)
    val subject = enumerationByName("subject", 50, Subject::class)
    val startedAt = timestamp("started_at").nullable()
    val finishedAt = timestamp("finished_at").nullable()
    val score = integer("score").nullable()

    override val primaryKey = PrimaryKey(id)
}
