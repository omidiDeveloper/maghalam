package com.example.maghalam.model.net.api

import com.example.maghalam.model.net.dto.response.UserProfileResponse
import retrofit2.Response
import retrofit2.http.*

interface UserApiService {

    @GET("api/users/profile")
    suspend fun getUserProfile(): Response<UserProfileResponse>

    @PUT("api/users/profile")
    suspend fun updateProfile(
        @Body request: Map<String, String>
    ): Response<UserProfileResponse>

    @PUT("api/users/settings")
    suspend fun updateSettings(
        @Body request: Map<String, Any>
    ): Response<UserProfileResponse>

    @PUT("api/users/password")
    suspend fun changePassword(
        @Body request: Map<String, String>
    ): Response<Unit>

    @DELETE("api/users/account")
    suspend fun deleteAccount(): Response<Unit>
}
