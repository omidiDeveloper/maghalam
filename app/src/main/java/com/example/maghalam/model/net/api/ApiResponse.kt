package com.example.maghalam.model.net.api

import retrofit2.Response


// Wrapper جنریک برای همه response های API
sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val code: Int, val message: String) : ApiResponse<Nothing>()
    data object Loading : ApiResponse<Nothing>()
    data object NetworkError : ApiResponse<Nothing>()
}


fun <T> Response<T>.toApiResponse(): ApiResponse<T> {
    return try {
        if (isSuccessful) {
            body()?.let {
                ApiResponse.Success(it)
            } ?: ApiResponse.Error(code(), "Empty response body")
        } else {
            ApiResponse.Error(code(), errorBody()?.string() ?: "Unknown error")
        }
    } catch (e: Exception) {
        ApiResponse.Error(-1, e.message ?: "Unknown error")
    }
}
