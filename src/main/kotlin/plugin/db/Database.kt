//package com.example.plugin.db
//
//import com.example.data.db.DatabaseFactory
//import io.ktor.server.application.Application
//import org.jetbrains.exposed.v1.jdbc.Database
//
//fun Application.configureDatabases() {
//    val driverClass=environment.config.property("storage.driverClassName").getString()
//    val jdbcUrl=environment.config.property("storage.jdbcURL").getString()
//    val db= Database.connect(DatabaseFactory.(jdbcUrl,driverClass))
//    transaction(db){
//        SchemaUtils.create(Users,Cities)
//    }
//}