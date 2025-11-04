package com.km9.notes.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class NoteRequest(
    val id: String? = null,

    @field:NotBlank(message = "Title is required")
    @field:Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    val title: String,

    @field:NotBlank(message = "Description is required")
    @field:Size(min = 1, max = 5000, message = "Description must be between 1 and 5000 characters")
    val description: String
)
