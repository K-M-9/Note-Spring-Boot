package com.km9.notes.api.dto

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)