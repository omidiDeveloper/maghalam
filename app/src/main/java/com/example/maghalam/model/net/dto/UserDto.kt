package com.example.maghalam.model.net.dto

import com.example.maghalam.model.data.User
import com.example.maghalam.model.db.entity.UserEntity
import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("publishedArticlesCount")
    val publishedArticlesCount: Int,

    @SerializedName("darkMode")
    val darkMode: Boolean,

    @SerializedName("fontSize")
    val fontSize: String,

    @SerializedName("createdAt")
    val createdAt: String?
)

// Add this to your UserProfileResponse class or create extension function
fun UserProfileResponse.toUser(): User {
    return User(
        id = this.id,
        fullName = this.fullName,
        username = this.username,
        email = this.email,
        role = this.role,
        publishedArticlesCount = this.publishedArticlesCount,
        darkMode = this.darkMode,
        fontSize = this.fontSize,
        createdAt = this.createdAt
    )
}

fun UserProfileResponse.toEntity(): UserEntity {
    return UserEntity(
        id = this.id ?: 0L,
        fullName = this.fullName,
        username = this.username,
        email = this.email,
        role = this.role,
        publishedArticlesCount = this.publishedArticlesCount,
        darkMode = this.darkMode,
        fontSize = this.fontSize,
        createdAt = this.createdAt
    )
}