package com.example.maghalam.model.net.dto.response

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