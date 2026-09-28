package com.prostuti.server.db.tables

import com.prostuti.core.model.Role
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object UsersTable : Table("users") {
    val id = uuid("id")
    val name = text("name")
    val email = text("email").uniqueIndex()
    val passwordHash = text("password_hash")
    val role = enumerationByName("role", 50, Role::class)
    val avatarId = varchar("avatar_id", 50).default("mascot_1")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}
