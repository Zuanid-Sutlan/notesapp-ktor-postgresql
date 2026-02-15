package com.example.domain.repository


import com.example.domain.model.dto.Note
import com.example.domain.model.request.NoteRequest
import java.util.UUID

interface NoteRepository {
    suspend fun createNote(ownerEmail: String, noteReq: NoteRequest): Note?
    suspend fun updateNote(noteId: Int, noteReq: NoteRequest, owner: String): Note?
    suspend fun deleteNote(noteId: Int, owner: String): Boolean
    suspend fun getAllNotes(): List<Note>?
    suspend fun getNote(noteId: Int, owner: String): Note?
    suspend fun getAllNotesByUser(ownerEmail: String): List<Note>?
}
