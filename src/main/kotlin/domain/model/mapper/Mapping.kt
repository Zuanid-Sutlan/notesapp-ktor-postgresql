package com.example.domain.model.mapper

import com.example.data.db.tables.NotesDAO
import com.example.data.db.tables.UserDAO
import com.example.domain.model.dto.Note
import com.example.domain.model.dto.User
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
fun noteDaoToModel(notesDAO: NotesDAO) = Note(
    id = notesDAO.noteId.value,
//    owner = notesDAO.owner,
    title = notesDAO.title,
    content = notesDAO.content,
    createdAt = notesDAO.createdAt.toString(),
    updatedAt = notesDAO.updatedAt.toString()
)

fun userDaoToModel(userDAO: UserDAO) = User(
    id = userDAO.userId.value,
    username = userDAO.username,
    email = userDAO.email,
    password = userDAO.password,
)

