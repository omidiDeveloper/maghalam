package com.example.maghalam.model.repository.user


import com.example.maghalam.model.data.User
import com.example.maghalam.model.net.api.ApiResponse
import com.example.maghalam.model.net.api.AuthApiService
import com.example.maghalam.model.net.api.UserApiService
import com.example.maghalam.model.net.api.toApiResponse
import com.example.maghalam.model.net.dto.request.LoginRequest
import com.example.maghalam.model.net.dto.request.RegisterRequest
import com.example.maghalam.model.net.dto.response.AuthResponse
import com.example.maghalam.utills.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UserRepositoryImpl(
    private val authApiService: AuthApiService,
    private val userApiService: UserApiService,
    private val sharedPreferencesManager: SharedPreferencesManager
) : UserRepository {


    override fun login(request: LoginRequest): Flow<ApiResponse<AuthResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = authApiService.login(request).toApiResponse()
            if (response is ApiResponse.Success) {
                // ذخیره token و اطلاعات کاربر
                sharedPreferencesManager.saveToken(response.data.accessToken)
                sharedPreferencesManager.saveToken(response.data.refreshToken)
                sharedPreferencesManager.saveToken(response.data.user.toString())
            }
            emit(response)
        } catch (e: Exception) {
            emit(ApiResponse.NetworkError)
        }
    }.flowOn(Dispatchers.IO)

    override fun register(request: RegisterRequest): Flow<ApiResponse<AuthResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = authApiService.register(request).toApiResponse()
            if (response is ApiResponse.Success) {
                sharedPreferencesManager.saveToken(response.data.accessToken)
                sharedPreferencesManager.saveRefreshToken(response.data.refreshToken)
                sharedPreferencesManager.saveUserInfo(response.data.user.toString() , response.data.user.id!!)
            }
            emit(response)
        } catch (e: Exception) {
            emit(ApiResponse.NetworkError)
        }
    }.flowOn(Dispatchers.IO)

    override fun logout(): Flow<ApiResponse<Unit>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = authApiService.logout().toApiResponse()
            // پاک کردن داده‌های محلی صرف نظر از نتیجه
            sharedPreferencesManager.clearAll()
            emit(response)
        } catch (e: Exception) {
            sharedPreferencesManager.clearAll()
            emit(ApiResponse.NetworkError)
        }
    }.flowOn(Dispatchers.IO)

    override fun getUserProfile(): Flow<ApiResponse<User>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = userApiService.getUserProfile()
            if (response.isSuccessful) {
                val profileResponse = response.body()
                if (profileResponse != null) {
                    // تبدیل UserProfileResponse به User
                    val user = User(
                        id = profileResponse.id,
                        fullName = profileResponse.fullName,
                        username = profileResponse.username,
                        email = profileResponse.email,
                        role = profileResponse.role,
                        publishedArticlesCount = profileResponse.publishedArticlesCount,
                        darkMode = profileResponse.darkMode,
                        fontSize = profileResponse.fontSize,
                        createdAt = profileResponse.createdAt
                    )
                    sharedPreferencesManager.saveUserInfo(user.username , user.id!!)
                    emit(ApiResponse.Success(user))
                } else {
                    emit(ApiResponse.Error(response.code(), "Empty response"))
                }
            } else {
                emit(ApiResponse.Error(response.code(), response.errorBody()?.string() ?: "Error"))
            }
        } catch (e: Exception) {
            emit(ApiResponse.NetworkError)
        }
    }.flowOn(Dispatchers.IO)

    override fun updateProfile(fullName: String, email: String): Flow<ApiResponse<User>> = flow {
        emit(ApiResponse.Loading)
        try {
            val body = mapOf("fullName" to fullName, "email" to email)
            val response = userApiService.updateProfile(body)
            if (response.isSuccessful) {
                response.body()?.let { profileResponse ->
                    val user = User(
                        id = profileResponse.id,
                        fullName = profileResponse.fullName,
                        username = profileResponse.username,
                        email = profileResponse.email,
                        role = profileResponse.role,
                        publishedArticlesCount = profileResponse.publishedArticlesCount,
                        darkMode = profileResponse.darkMode,
                        fontSize = profileResponse.fontSize,
                        createdAt = profileResponse.createdAt
                    )
                    sharedPreferencesManager.saveUserInfo(user.username , user.id!!)
                    emit(ApiResponse.Success(user))
                } ?: emit(ApiResponse.Error(response.code(), "Empty response"))
            } else {
                emit(ApiResponse.Error(response.code(), response.errorBody()?.string() ?: "Error"))
            }
        } catch (e: Exception) {
            emit(ApiResponse.NetworkError)
        }
    }.flowOn(Dispatchers.IO)

    override fun updateSettings(darkMode: Boolean, fontSize: String): Flow<ApiResponse<User>> = flow {
        emit(ApiResponse.Loading)
        try {
            val body = mapOf<String, Any>("darkMode" to darkMode, "fontSize" to fontSize)
            val response = userApiService.updateSettings(body)
            if (response.isSuccessful) {
                response.body()?.let { profileResponse ->
                    val user = User(
                        id = profileResponse.id,
                        fullName = profileResponse.fullName,
                        username = profileResponse.username,
                        email = profileResponse.email,
                        role = profileResponse.role,
                        publishedArticlesCount = profileResponse.publishedArticlesCount,
                        darkMode = profileResponse.darkMode,
                        fontSize = profileResponse.fontSize,
                        createdAt = profileResponse.createdAt
                    )
                    sharedPreferencesManager.saveUserInfo(user.username , user.id!!)
                    emit(ApiResponse.Success(user))
                } ?: emit(ApiResponse.Error(response.code(), "Empty response"))
            } else {
                emit(ApiResponse.Error(response.code(), response.errorBody()?.string() ?: "Error"))
            }
        } catch (e: Exception) {
            emit(ApiResponse.NetworkError)
        }
    }.flowOn(Dispatchers.IO)

    override fun changePassword(currentPassword: String, newPassword: String): Flow<ApiResponse<Unit>> = flow {
        emit(ApiResponse.Loading)
        try {
            val body = mapOf("currentPassword" to currentPassword, "newPassword" to newPassword)
            emit(userApiService.changePassword(body).toApiResponse())
        } catch (e: Exception) {
            emit(ApiResponse.NetworkError)
        }
    }.flowOn(Dispatchers.IO)
}

