package com.example.maghalam.model.net.dto.response


import com.google.gson.annotations.SerializedName

sealed class ApiResponse<out T> {
    object Loading : ApiResponse<Nothing>()
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val code: Int, val message: String) : ApiResponse<Nothing>()

    val isSuccess get() = this is Success
    val isError get() = this is Error
    val isLoading get() = this is Loading
}
