package com.km9.notes.api.controller

import com.km9.notes.api.dto.AuthRequest
import com.km9.notes.api.dto.AuthResponse
import com.km9.notes.api.dto.RefreshRequest
import com.km9.notes.service.AuthService
import com.km9.notes.shared.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody body: AuthRequest
    ): ResponseEntity<ApiResponse<String>> {
        val user = authService.register(body.email, body.password)

        val response = ApiResponse.success(
            data = user.id.toHexString(),
            message = "User registered successfully"
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody body: AuthRequest
    ): ResponseEntity<ApiResponse<AuthResponse>> {
        val authResponse = authService.login(body.email, body.password)

        val response = ApiResponse.success(
            data = authResponse,
            message = "Login successful"
        )

        return ResponseEntity.ok(response)
    }

    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody body: RefreshRequest
    ): ResponseEntity<ApiResponse<AuthResponse>> {
        val authResponse = authService.refresh(body.refreshToken)

        val response = ApiResponse.success(
            data = authResponse,
            message = "Token refreshed successfully"
        )

        return ResponseEntity.ok(response)
    }
}