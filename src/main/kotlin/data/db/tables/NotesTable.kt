package com.example.data.db.tables

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.javatime.timestamp
import java.util.UUID

object NotesTable : IntIdTable(name = "notes") {
    val owner = varchar("owner_email", 128)
    val title = varchar("title", length = 200)
    val content = text("content").nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
}

class NotesDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<NotesDAO>(NotesTable)
    var noteId by NotesTable.id
    var owner by NotesTable.owner
    var title by NotesTable.title
    var content by NotesTable.content
    var createdAt by NotesTable.createdAt
    var updatedAt by NotesTable.updatedAt
}