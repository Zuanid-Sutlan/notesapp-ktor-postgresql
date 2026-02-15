package com.example.domain.model.request

import kotlinx.serialization.Serializable

@Serializable
data class NoteRequest(
    val title: String,
    val description: String
)