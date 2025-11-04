package com.km9.notes.shared


import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(
        ex: BaseException,
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Base exception occurred: ${ex.message}", ex)

        val response = if (ex is ValidationException) {
            ApiResponse.error<Nothing>(
                message = ex.message ?: "Validation failed",
                errors = ex.errors,
            )
        } else {
            ApiResponse.error<Nothing>(
                message = ex.message ?: "An error occurred",
            )
        }

        return ResponseEntity.status(ex.status).body(response)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Validation error: ${ex.message}")

        val errors = ex.bindingResult.allErrors.map { error ->
            val fieldName = (error as? FieldError)?.field ?: "unknown"
            val message = error.defaultMessage ?: "Invalid value"
            "$fieldName: $message"
        }

        val response = ApiResponse.error<Nothing>(
            message = "Validation failed",
            errors = errors,
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Malformed JSON: ${ex.message}")

        val response = ApiResponse.error<Nothing>(
            message = "Malformed JSON request",
            error = "Please check your request body format",
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
        ex: MethodArgumentTypeMismatchException,
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Type mismatch: ${ex.message}")

        val response = ApiResponse.error<Nothing>(
            message = "Invalid parameter type",
            error = "Parameter '${ex.name}' should be of type ${ex.requiredType?.simpleName}",
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNotFound(
        ex: NoHandlerFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Endpoint not found: ${ex.message}")

        val response = ApiResponse.error<Nothing>(
            message = "Endpoint not found",
            error = "The requested URL ${request.requestURI} was not found",
        )

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
    ): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Unexpected error occurred: ${ex.message}", ex)

        val response = ApiResponse.error<Nothing>(
            message = "An unexpected error occurred",
            error = ex.message ?: "Internal server error",
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }
}
