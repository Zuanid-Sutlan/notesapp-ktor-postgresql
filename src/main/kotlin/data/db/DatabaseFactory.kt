package com.example.data.db

import com.example.data.db.tables.NotesTable
import com.example.data.db.tables.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.Transaction
import org.jetbrains.exposed.v1.javatime.timestamp
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import java.util.UUID

object DatabaseFactory {

    private const val DATABASE_NAME = "my_notes_db"
    private const val USERNAME = "admin"
    private const val PASSWORD = "secret"

    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://localhost:5432/$DATABASE_NAME"
        driverClassName = "org.postgresql.Driver"
        username = USERNAME
        password = PASSWORD
        maximumPoolSize = 7
        isReadOnly = false
        transactionIsolation = "TRANSACTION_SERIALIZABLE"
    }

    private val dataSource = HikariDataSource(config)

    val database = Database.connect(datasource = dataSource)

    fun init() {
        transaction(database) {
            SchemaUtils.create(UserTable)
            SchemaUtils.create(NotesTable)
        }
//        transaction(database) {
//            SchemaUtils.create(NotesTable)
//        }
//        dbQuery {
//            UserTable.insert {
//                it[displayName] = "Sultan"
//                it[email] = "sultan@mail.com"
//                it[passwordHash] = "sultan"
//            }
//            NotesTable.insert {
//                it[NotesTable.title] = "dummy title"
//                it[NotesTable.content] = "dummy content"
//                it[NotesTable.createdAt] = Instant.now()
//                it[NotesTable.updatedAt] = Instant.now()
//            }
//        }
    }

//    val database = Database.connect(
//        url = "jdbc:postgresql://localhost:5432/my_database",
//        driver = "org.postgresql.Driver",
//        user = "admin",
//        password = "secret"
//    )

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction(database) { block() }
        }

    suspend fun <T> suspendTransaction(block: Transaction.() -> T): T {
//        addLogger(org.jetbrains.exposed.sql.StdOutSqlLogger)
        return newSuspendedTransaction(Dispatchers.IO, statement = block)
    }
}
