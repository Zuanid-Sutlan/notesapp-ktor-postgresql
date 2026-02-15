package com.example.domain.model.dto

import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class Note @OptIn(ExperimentalTime::class) constructor(
    val id: Int,
//    val owner: String,
    val title: String,
    val content: String?,
    val createdAt: String,
    val updatedAt: String
)