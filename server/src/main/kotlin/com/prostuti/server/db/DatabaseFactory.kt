package com.prostuti.server.db

import com.prostuti.server.db.tables.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val dotenv = dotenv {
            ignoreIfMissing = true
            directory = "./"
        }
        val dotenvParent = dotenv {
            ignoreIfMissing = true
            directory = "../"
        }

        val dbUrlRaw = dotenv["DB_URL"] ?: dotenvParent["DB_URL"] ?: System.getenv("DB_URL") ?: error("DB_URL is not set")
        
        var cleanJdbcUrl = dbUrlRaw
        var dbUser = dotenv["DB_USER"] ?: dotenvParent["DB_USER"] ?: System.getenv("DB_USER")
        var dbPassword = dotenv["DB_PASSWORD"] ?: dotenvParent["DB_PASSWORD"] ?: System.getenv("DB_PASSWORD")

        if (dbUrlRaw.startsWith("postgres://") || dbUrlRaw.startsWith("postgresql://")) {
            val uri = java.net.URI(dbUrlRaw)
            val host = uri.host
            val port = if (uri.port != -1) uri.port else 5432
            val path = uri.path
            val query = uri.query?.let { "?$it" } ?: ""
            
            cleanJdbcUrl = "jdbc:postgresql://$host:$port$path$query"
            
            if (uri.userInfo != null) {
                val parts = uri.userInfo.split(":")
                dbUser = parts[0]
                if (parts.size > 1) {
                    dbPassword = parts[1]
                }
            }
        } else if (!cleanJdbcUrl.startsWith("jdbc:")) {
            cleanJdbcUrl = "jdbc:$cleanJdbcUrl"
        }

        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = cleanJdbcUrl
            if (dbUser != null) username = dbUser
            if (dbPassword != null) password = dbPassword
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                UsersTable,
                QuestionsTable,
                PracticeSessionsTable,
                PracticeSessionQuestionsTable,
                ExamAttemptsTable,
                ExamAttemptQuestionsTable,
                AnswersTable
            )
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
