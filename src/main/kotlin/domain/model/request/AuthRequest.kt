package com.example.domain.model.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(val username: String?, val email: String, val password: String)