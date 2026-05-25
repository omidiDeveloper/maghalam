package com.example.maghalam.model.net.dto.request

data class RegisterRequest(
    val fullName: String,
    val username: String,
    val email: String,
    val password: String
)
