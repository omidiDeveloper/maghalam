package com.example.maghalam.model.net.dto

import com.example.maghalam.model.data.User
import com.google.gson.annotations.SerializedName

//------------------------------------------------

data class LoginRequest(
    val username: String,
    val password: String
)

//------------------------------------------------

data class RegisterRequest(
    val fullName: String,
    val username: String,
    val email: String,
    val password: String
)

// ------------------------------------------------

data class AuthResponse(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String,

    @SerializedName("user")
    val user: User
)

//------------------------------------------------

class AuthDto {
}