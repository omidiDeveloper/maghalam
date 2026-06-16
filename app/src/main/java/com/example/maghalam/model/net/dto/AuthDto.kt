package com.example.maghalam.model.net.dto

import com.google.gson.annotations.SerializedName

//------------------------------------------------

data class LoginRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String = username,

    @SerializedName("usernameOrEmail")
    val usernameOrEmail: String = username,

    val password: String
)

//------------------------------------------------

data class RegisterRequest(
    val fullName: String,
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)

// ------------------------------------------------

data class AuthResponse(
    @SerializedName(value = "token", alternate = ["accessToken", "jwt"])
    val accessToken: String? = null,

    @SerializedName(value = "refreshToken", alternate = ["refresh_token"])
    val refreshToken: String? = null,

    val type: String? = null,

    val user: UserProfileResponse? = null,

    val id: Long? = null,

    val fullName: String? = null,

    val username: String? = null,

    val email: String? = null,

    val role: String? = null,

    val publishedArticlesCount: Int = 0,

    val darkMode: Boolean = false,

    val fontSize: String? = null,

    val createdAt: String? = null
) {
    fun userProfile(fallbackUsername: String): UserProfileResponse {
        user?.let { return it }

        return UserProfileResponse(
            id = id ?: -1L,
            fullName = fullName.orEmpty(),
            username = username ?: fallbackUsername,
            email = email.orEmpty(),
            role = role ?: "USER",
            publishedArticlesCount = publishedArticlesCount,
            darkMode = darkMode,
            fontSize = fontSize ?: "Vazir",
            createdAt = createdAt
        )
    }
}

//------------------------------------------------

class AuthDto {
}
