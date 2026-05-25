package com.example.maghalam.model.data

import com.google.gson.annotations.SerializedName

data class Article(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("title")
    val title: String,

    @SerializedName("author")
    val author: String,

    @SerializedName("keywords")
    val keywords: String,

    @SerializedName("language")
    val language: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("content")
    val content: String = "",

    @SerializedName("abstract")
    val abstract: String = "",

    @SerializedName("wordCount")
    val wordCount: Int = 0,

    @SerializedName("isPublished")
    val isPublished: Boolean = false,

    @SerializedName("createdAt")
    val createdAt: String? = null, // ISO 8601 format: "2025-05-23T10:30:00"

    @SerializedName("userId")
    val userId: Long
) {
    // Helper function برای نمایش کلمات کلیدی به صورت لیست
    fun getKeywordsList(): List<String> {
        return keywords.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    // Helper function برای نمایش تاریخ به صورت خوانا
    fun getFormattedDate(): String {
        return createdAt?.take(10) ?: "" // فقط تاریخ بدون ساعت
    }
}