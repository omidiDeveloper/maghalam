package com.example.maghalam.model.net.api


import com.example.maghalam.model.net.dto.request.LoginRequest
import com.example.maghalam.model.net.dto.request.RegisterRequest
import com.example.maghalam.model.net.dto.response.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("api/auth/refresh")
    suspend fun refreshToken(
        @Body request: Map<String, String>
    ): Response<AuthResponse>

    @POST("api/auth/logout")
    suspend fun logout(): Response<Unit>
}
