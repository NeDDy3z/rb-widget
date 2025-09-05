package com.vanek.rbwidget.model.network.response

/**
 * Generic API response wrapper
 */
data class ApiResponse<T>(
    val isSuccess: Boolean,
    val data: T? = null,
    val errorMessage: String? = null,
    val statusCode: Int? = null
) {
    companion object {
        fun <T> success(data: T, statusCode: Int = 200): ApiResponse<T> {
            return ApiResponse(
                isSuccess = true,
                data = data,
                statusCode = statusCode
            )
        }

        fun <T> error(message: String, statusCode: Int? = null): ApiResponse<T> {
            return ApiResponse(
                isSuccess = false,
                errorMessage = message,
                statusCode = statusCode
            )
        }
    }
}