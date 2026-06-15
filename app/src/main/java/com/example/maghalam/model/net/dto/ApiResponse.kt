package com.example.maghalam.model.net.dto

sealed class ApiResponse<out T>{

    data object Loading : ApiResponse<Nothing>()

    data class Success<T>(
        val data:T
    ): ApiResponse<T>()

    data class Error(
        val code:Int,
        val message:String
    ): ApiResponse<Nothing>()

    data object NetworkError : ApiResponse<Nothing>()

}