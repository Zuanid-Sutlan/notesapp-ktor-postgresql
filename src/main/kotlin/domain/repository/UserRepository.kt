package com.example.domain.repository

import com.example.data.db.tables.UserDAO
import com.example.data.db.tables.UserTable
import com.example.domain.model.dto.User
import com.example.domain.model.request.AuthRequest

interface UserRepository {
    suspend fun createUser(authRequest: AuthRequest): User?
    suspend fun updateUser(userId: Int, authRequest: AuthRequest): User?
    suspend fun deleteUser(userId: Int): Boolean
    suspend fun getUserByEmail(email: String): User?
    suspend fun getAllUsers(): List<User>?
}