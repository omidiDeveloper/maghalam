package com.example.maghalam.model.repository.user

import com.example.maghalam.model.data.User
import com.example.maghalam.model.net.dto.ApiResponse
import com.example.maghalam.model.net.dto.AuthResponse
import com.example.maghalam.model.net.dto.LoginRequest
import com.example.maghalam.model.net.dto.RegisterRequest
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun login(request: LoginRequest): Flow<ApiResponse<AuthResponse>>
    fun register(request: RegisterRequest): Flow<ApiResponse<AuthResponse>>
    fun logout(): Flow<ApiResponse<Unit>>
    fun getUserProfile(): Flow<ApiResponse<User>>
    fun updateProfile(fullName: String, email: String): Flow<ApiResponse<User>>
    fun updateSettings(darkMode: Boolean, fontSize: String): Flow<ApiResponse<User>>
    fun changePassword(currentPassword: String, newPassword: String): Flow<ApiResponse<Unit>>
    fun getUsers(): Flow<ApiResponse<List<User>>>
    fun deleteUser(userId: Long): Flow<ApiResponse<Unit>>
}
