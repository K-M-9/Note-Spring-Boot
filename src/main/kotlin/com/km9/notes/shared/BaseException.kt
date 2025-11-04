package com.km9.notes.shared

import org.springframework.http.HttpStatus

sealed class BaseException(
    message: String,
    val status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
) : RuntimeException(message)

class ResourceNotFoundException(
    message: String = "Resource not found"
) : BaseException(message, HttpStatus.NOT_FOUND)

class BadRequestException(
    message: String = "Bad request"
) : BaseException(message, HttpStatus.BAD_REQUEST)

class UnauthorizedException(
    message: String = "Unauthorized access"
) : BaseException(message, HttpStatus.UNAUTHORIZED)

class ForbiddenException(
    message: String = "Access forbidden"
) : BaseException(message, HttpStatus.FORBIDDEN)

class ConflictException(
    message: String = "Resource conflict"
) : BaseException(message, HttpStatus.CONFLICT)

class ValidationException(
    message: String = "Validation failed",
    val errors: List<String> = emptyList()
) : BaseException(message, HttpStatus.UNPROCESSABLE_ENTITY)