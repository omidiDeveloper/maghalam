package com.example.maghalam.model.data

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("fullName")
    var fullName: String,

    @SerializedName("username")
    var username: String,

    @SerializedName("role")
    var role: String = "USER", // USER یا ADMIN

    @SerializedName("email")
    var email: String,

    @SerializedName("publishedArticlesCount")
    var publishedArticlesCount: Int = 0,

    @SerializedName("darkMode")
    var darkMode: Boolean = false,

    @SerializedName("fontSize")
    var fontSize: String = "Vazir",

    @SerializedName("createdAt")
    val createdAt: String? = null // ISO 8601 format: "2025-05-23T10:30:00"
) {
    // Helper function برای چک کردن نقش ادمین
    fun isAdmin(): Boolean = role == "ADMIN"

    // Helper function برای فرمت تاریخ ثبت‌نام
    fun getFormattedCreatedDate(): String {
        return createdAt?.let {
            try {
                it.substring(0, 10) // فقط تاریخ: "2025-05-23"
            } catch (e: Exception) {
                it
            }
        } ?: ""
    }
}
