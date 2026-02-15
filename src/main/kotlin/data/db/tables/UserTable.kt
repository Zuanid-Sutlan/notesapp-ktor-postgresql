package com.example.data.db.tables

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.dao.UUIDEntity
import java.util.UUID

object UserTable: IntIdTable("users") {
//    val id = integer("id").autoIncrement()
    val email = varchar("email", 128).uniqueIndex()
    val username = varchar("username", 256)
    val password = varchar("password", 64)

//    override val primaryKey: PrimaryKey?
//        get() = PrimaryKey(id)
}

class UserDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDAO>(UserTable)
    var userId by UserTable.id
    var email by UserTable.email
    var username by UserTable.username
    var password by UserTable.password
}