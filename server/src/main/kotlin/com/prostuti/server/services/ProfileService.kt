package com.prostuti.server.services

import com.prostuti.core.model.UpdateProfileRequest
import com.prostuti.core.model.UserProfileDto
import com.prostuti.server.db.DatabaseFactory.dbQuery
import com.prostuti.server.db.tables.UsersTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.util.UUID

class ProfileService {

    suspend fun getProfile(userId: String): UserProfileDto = dbQuery {
        val userUuid = runCatching { UUID.fromString(userId) }.getOrNull()
            ?: throw IllegalArgumentException("Invalid user ID")

        val row = UsersTable
            .selectAll()
            .where { UsersTable.id eq userUuid }
            .limit(1)
            .firstOrNull()
            ?: throw NoSuchElementException("User not found")

        UserProfileDto(
            id = row[UsersTable.id].toString(),
            name = row[UsersTable.name],
            email = row[UsersTable.email],
            role = row[UsersTable.role],
            avatarId = row[UsersTable.avatarId],
            createdAt = row[UsersTable.createdAt].toString(),
        )
    }

    suspend fun updateProfile(userId: String, request: UpdateProfileRequest): UserProfileDto {
        require(request.name.isNotBlank()) { "Name must not be blank" }
        require(request.name.length in 1..100) { "Name must be between 1 and 100 characters" }
        require(request.avatarId.isNotBlank()) { "Avatar must not be blank" }

        val trimmedName = request.name.trim()
        val trimmedAvatar = request.avatarId.trim()

        return dbQuery {
            val userUuid = runCatching { UUID.fromString(userId) }.getOrNull()
                ?: throw IllegalArgumentException("Invalid user ID")

            val updatedRows = UsersTable.update({ UsersTable.id eq userUuid }) {
                it[name] = trimmedName
                it[avatarId] = trimmedAvatar
            }

            if (updatedRows == 0) {
                throw NoSuchElementException("User not found")
            }

            val row = UsersTable
                .selectAll()
                .where { UsersTable.id eq userUuid }
                .limit(1)
                .firstOrNull()
                ?: throw NoSuchElementException("User not found")

            UserProfileDto(
                id = row[UsersTable.id].toString(),
                name = row[UsersTable.name],
                email = row[UsersTable.email],
                role = row[UsersTable.role],
                avatarId = row[UsersTable.avatarId],
                createdAt = row[UsersTable.createdAt].toString(),
            )
        }
    }
}
