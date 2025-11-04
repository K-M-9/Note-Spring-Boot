package com.km9.notes.shared


import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val errors: List<String>? = null,
) {
    companion object {
        fun <T> success(data: T, message: String = "Success"): ApiResponse<T> {
            return ApiResponse(
                success = true,
                message = message,
                data = data
            )
        }

        fun <T> success(message: String = "Success"): ApiResponse<T> {
            return ApiResponse(
                success = true,
                message = message
            )
        }

        fun <T> error(message: String, errors: List<String>? = null): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = message,
                errors = errors,
            )
        }

        fun <T> error(message: String, error: String): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = message,
                errors = listOf(error),
            )
        }
    }
}