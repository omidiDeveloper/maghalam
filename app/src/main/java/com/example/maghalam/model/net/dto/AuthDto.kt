package com.example.maghalam.model.net.dto

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement

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

    val roles: JsonElement? = null,

    val authorities: JsonElement? = null,

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
            role = role ?: extractRole(roles) ?: extractRole(authorities) ?: "USER",
            publishedArticlesCount = publishedArticlesCount,
            darkMode = darkMode,
            fontSize = fontSize ?: "Vazir",
            createdAt = createdAt
        )
    }

    private fun extractRole(source: JsonElement?): String? {
        if (source == null || source.isJsonNull) return null

        return when {
            source.isJsonPrimitive -> source.asString
            source.isJsonArray -> source.asJsonArray.firstNotNullOfOrNull { extractRole(it) }
            source.isJsonObject -> {
                val json = source.asJsonObject
                listOf("role", "name", "authority")
                    .firstNotNullOfOrNull { key ->
                        json.get(key)?.takeIf { !it.isJsonNull }?.asString
                    }
            }
            else -> null
        }
    }
}

//------------------------------------------------

class AuthDto {
}
