package com.km9.notes.api.dto

import java.time.Instant

data class NoteResponse(
    val id: String,
    val title: String,
    val description: String,
    val createdAt: Instant
)
