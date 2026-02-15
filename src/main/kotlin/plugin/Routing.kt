package com.example.plugin

import com.example.domain.model.request.AuthRequest
import com.example.domain.model.request.NoteRequest
import com.example.domain.repository.NoteRepository
import com.example.domain.repository.UserRepository
import com.example.plugin.auth.UserSession
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlinx.serialization.Serializable
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

fun Application.configureRouting(userRepository: UserRepository, noteRepository: NoteRepository) {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        post("/register") {
            val requestData = call.receive<AuthRequest>()
            val allUsers = userRepository.getAllUsers()
            val isUserExist = allUsers?.find { it.email == requestData.email }
            if (isUserExist != null) {
                call.respond(HttpStatusCode.Conflict, "User already exists")
                return@post
            }

//            userDb[requestData.email] = requestData.password
            userRepository.createUser(requestData).let {
                if (it != null) {
                    call.sessions.set(UserSession(requestData.email))
                    call.respond(HttpStatusCode.Created, "Signup successful")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Signup failed")
                }
            }
        }

        post("/login") {
            val requestData = call.receive<AuthRequest>()
            val user = userRepository.getUserByEmail(requestData.email)
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Username not Found")
                return@post
            }
//            val storedPassword = userDb[requestData.username]

//            if (storedPassword == null) {
//                call.respond(HttpStatusCode.NotFound, "User doesn't exist")
//                return@post
//            }

            if (user.password != requestData.password) {
                call.respond(HttpStatusCode.Unauthorized, "Incorrect password")
                return@post
            }

            call.sessions.set(UserSession(requestData.email))
            call.respond(HttpStatusCode.OK, "Login successful")
        }

        authenticate("session-auth") {

            post("/logout") {
                call.sessions.clear<UserSession>()
                call.respond(HttpStatusCode.OK, "Logout successful")
            }

            route("/notes") {

                // ✅ Add note
                post {
                    val user = call.requireEmail()
                    val req = call.receive<NoteRequest>()

                    val isInserted = noteRepository.createNote(
                        ownerEmail = user,
                        noteReq = req
                    )
                    if (isInserted != null) {
                        call.respond(HttpStatusCode.Created, isInserted)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Not inserted")
                    }
                }

                // ✅ Get all notes for current user
                get {
                    val user = call.requireEmail()

                    val notes = noteRepository.getAllNotesByUser(user)

                    if (notes != null) {
                        call.respond(HttpStatusCode.OK, notes)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Not found")
                    }
                }

                // ✅ Get note by id
                get("{id}") {
                    val user = call.requireEmail()
                    val id = call.parameters["id"] ?: run {
                        call.respond(HttpStatusCode.BadRequest, "Missing id")
                        return@get
                    }

                    val intId = id.toIntOrNull()
                    if (intId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Id '$intId' is invalid")
                        return@get
                    }

                    val note = noteRepository.getNote(intId, user)

                    if (note == null) {
                        call.respond(HttpStatusCode.NotFound, "Note not found")
                        return@get
                    }

                    call.respond(HttpStatusCode.OK, note)
                }

                // ✅ delete note by id
                delete("{id}") {
                    val user = call.requireEmail()
                    val id = call.parameters["id"] ?: run {
                        call.respond(HttpStatusCode.BadRequest, "Missing id type int")
                        return@delete
                    }

                    val intId = id.toIntOrNull()
                    if (intId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Missing id")
                        return@delete
                    }

                    val isDeleted = noteRepository.deleteNote(intId, user)

                    if (isDeleted) {
                        call.respond(HttpStatusCode.OK, "deleted note $intId Successfully")
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Not deleted")
                    }
                }

                // ✅ update note by id
                put("{id}") {
                    val user = call.requireEmail()
                    val req = call.receive<NoteRequest>()
                    val id = call.parameters["id"] ?: run {
                        call.respond(HttpStatusCode.BadRequest, "Missing id")
                        return@put
                    }

                    val intId = id.toIntOrNull()
                    if (intId == null) {
                        call.respond(HttpStatusCode.BadRequest, "Missing or invalid id: $intId")
                        return@put
                    }

                    val existing = noteRepository.updateNote(intId, req, user)
                    if (existing == null) {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to update note")
                        return@put
                    }

                    call.respond(HttpStatusCode.OK, existing)
                }
            }
        }
    }
}

/** Helper to safely fetch logged-in username */
private fun ApplicationCall.requireEmail(): String {
    val session = principal<UserSession>()
    if (session?.email.isNullOrBlank()) {
        throw IllegalStateException("Session principal missing. Check session-auth setup.")
    }
    return session!!.email
}

