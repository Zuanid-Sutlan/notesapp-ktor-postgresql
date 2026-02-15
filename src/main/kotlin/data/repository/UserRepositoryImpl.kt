package com.example.data.repository

import com.example.data.db.DatabaseFactory
import com.example.data.db.tables.UserDAO
import com.example.data.db.tables.UserTable
import com.example.domain.model.dto.User
import com.example.domain.model.mapper.userDaoToModel
import com.example.domain.model.request.AuthRequest
import com.example.domain.repository.UserRepository
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteWhere

class UserRepositoryImpl: UserRepository {
    override suspend fun createUser(authRequest: AuthRequest): User? {
        return try {
            val userDao = DatabaseFactory.suspendTransaction {
                UserDAO.new {
                    username = authRequest.username ?: "anonymous"
                    email = authRequest.email
                    password = authRequest.password
                }
            }
            userDaoToModel(userDao)
        } catch (e: Exception){
            print("User Creation Failed ${e.message}")
            null
        }
    }

    override suspend fun updateUser(userId: Int, authRequest: AuthRequest): User? {
        return try {
            val userDao = DatabaseFactory.suspendTransaction {
                UserDAO.findByIdAndUpdate(userId, { userDAO ->
                    userDAO.username = authRequest.username ?: "anonymous"
                    userDAO.email = authRequest.email
                    userDAO.password = authRequest.password
                })
            }
            if (userDao != null){
                userDaoToModel(userDao)
            }else{
                null
            }
        } catch (e: Exception){
            print("User Update Failed ${e.message}")
            null
        }
    }

    override suspend fun deleteUser(userId: Int): Boolean = DatabaseFactory.suspendTransaction{
        try {
            val deleted = UserTable.deleteWhere {
                UserTable.id eq userId
            }
            deleted == 1
        } catch (e: Exception){
            print("User Deletion Failed ${e.message}")
            false
        }
    }

    override suspend fun getUserByEmail(email: String): User? = DatabaseFactory.suspendTransaction {
        try {
            UserDAO
                .find { (UserTable.email eq email) }
                .limit(1)
                .map(::userDaoToModel)
                .firstOrNull()
        } catch (e: Exception) {
            print("Failed to get user by email ${e.message}")
            null
        }
    }

    override suspend fun getAllUsers(): List<User>? = DatabaseFactory.suspendTransaction {
        try {
            UserDAO
                .all()
                .map(::userDaoToModel)
        } catch (e: Exception) {
            print("Failed to fetch user from database ${e.message}")
            null
        }
    }


}