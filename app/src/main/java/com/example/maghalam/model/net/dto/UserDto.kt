package com.example.maghalam.model.net.dto

import com.example.maghalam.model.data.User
import com.example.maghalam.model.db.entity.UserEntity
import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("id")
    val id: Long = -1L,

    @SerializedName("fullName")
    val fullName: String? = "",

    @SerializedName("username")
    val username: String? = "",

    @SerializedName("email")
    val email: String? = "",

    @SerializedName("role")
    val role: String? = "USER",

    @SerializedName("publishedArticlesCount")
    val publishedArticlesCount: Int = 0,

    @SerializedName("darkMode")
    val darkMode: Boolean = false,

    @SerializedName("fontSize")
    val fontSize: String? = "Vazir",

    @SerializedName("createdAt")
    val createdAt: String?
)

// Add this to your UserProfileResponse class or create extension function
fun UserProfileResponse.toUser(): User {
    return User(
        id = this.id,
        fullName = this.fullName.orEmpty(),
        username = this.username.orEmpty(),
        email = this.email.orEmpty(),
        role = this.role ?: "USER",
        publishedArticlesCount = this.publishedArticlesCount,
        darkMode = this.darkMode,
        fontSize = this.fontSize ?: "Vazir",
        createdAt = this.createdAt
    )
}

fun UserProfileResponse.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        fullName = this.fullName.orEmpty(),
        username = this.username.orEmpty(),
        email = this.email.orEmpty(),
        role = this.role ?: "USER",
        publishedArticlesCount = this.publishedArticlesCount,
        darkMode = this.darkMode,
        fontSize = this.fontSize ?: "Vazir",
        createdAt = this.createdAt
    )
}
