package com.example

import com.example.data.db.DatabaseFactory
import com.example.data.repository.NoteRepositoryImpl
import com.example.data.repository.UserRepositoryImpl
import com.example.plugin.auth.configureSessionAuthentication
import com.example.plugin.auth.configureSessions
import com.example.plugin.configureMonitoring
import com.example.plugin.configureRouting
import com.example.plugin.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    val userRepository = UserRepositoryImpl()
    val noteRepository = NoteRepositoryImpl()
    configureMonitoring()
    configureSerialization()
    configureSessions()
    configureSessionAuthentication()
    configureRouting(userRepository, noteRepository)
}
