package com.example.data.repository

import com.example.data.db.DatabaseFactory
import com.example.data.db.tables.NotesDAO
import com.example.data.db.tables.NotesTable
import com.example.data.db.tables.NotesTable.owner
import com.example.data.db.tables.UserDAO
import com.example.data.db.tables.UserTable
import com.example.domain.model.dto.Note
import com.example.domain.model.mapper.noteDaoToModel
import com.example.domain.model.request.NoteRequest
import com.example.domain.repository.NoteRepository
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import java.util.UUID
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class NoteRepositoryImpl : NoteRepository {
    @OptIn(ExperimentalTime::class)
    override suspend fun createNote(ownerEmail: String, noteReq: NoteRequest): Note? {
        return try {
            val noteDao = DatabaseFactory.suspendTransaction {
                NotesDAO.new {
                    this.owner = ownerEmail
                    this.title = noteReq.title
                    this.content = noteReq.description
                    this.createdAt = java.time.Instant.now()
                    this.updatedAt = java.time.Instant.now()
                }
            }
            noteDaoToModel(noteDao)
        } catch (e: Exception) {
            print("Failed to add note to db ${e.message}")
            null
        }
    }

    override suspend fun updateNote(noteId: Int, noteReq: NoteRequest, owner: String): Note? {
        return try {
            val noteDao = DatabaseFactory.suspendTransaction {
                NotesDAO.findByIdAndUpdate(noteId, { noteDao ->

                    if (noteDao.owner == owner) {
                        noteDao.updatedAt = java.time.Instant.now()
                        noteDao.title = noteReq.title
                        noteDao.content = noteReq.description
                    }
                })
            }
            if (noteDao != null && noteDao.owner == owner) {
                noteDaoToModel(noteDao)
            } else {
                null
            }
        } catch (e: Exception) {
            print("failed to update note error: ${e.message}")
            null
        }
    }

    override suspend fun deleteNote(noteId: Int, owner: String): Boolean {
        return try {
            val deleted = DatabaseFactory.suspendTransaction {
                NotesTable.deleteWhere {
                    (NotesTable.id eq noteId) and (NotesTable.owner eq owner)
                }
            }
            deleted == 1
        } catch (e: Exception){
            print("note Deletion Failed ${e.message}")
            false
        }
    }

    override suspend fun getAllNotes(): List<Note>? {
        return try {
            DatabaseFactory.suspendTransaction {
                NotesDAO
                    .all()
                    .map(::noteDaoToModel)
            }
        } catch (e: Exception) {
            print("Failed to fetch data from database ${e.message}")
            null
        }
    }

    override suspend fun getNote(noteId: Int, owner: String): Note? {
        return try {
            DatabaseFactory.suspendTransaction {
                NotesDAO.find { (NotesTable.id eq noteId) and (NotesTable.owner eq owner) }
                    .limit(1)
                    .map(::noteDaoToModel)
                    .firstOrNull()
            }
        } catch (e: Exception) {
            print("note not Found ${e.message}")
            null
        }
    }

    override suspend fun getAllNotesByUser(ownerEmail: String): List<Note>? {
        return try {
            DatabaseFactory.suspendTransaction {
                NotesDAO
                    .find { (NotesTable.owner eq ownerEmail) }
                    .map(::noteDaoToModel)
            }
        } catch (e: Exception) {
            print("Failed to fetch note of a userid: $ownerEmail from database ${e.message}")
            null
        }
    }

}