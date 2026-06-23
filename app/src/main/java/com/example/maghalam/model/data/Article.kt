package com.example.maghalam.model.data

import com.example.maghalam.model.net.dto.KeywordStringAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

data class Article(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("title")
    val title: String = "",

    @SerializedName("author")
    val author: String = "",

    @SerializedName("keywords")
    @JsonAdapter(KeywordStringAdapter::class)
    val keywords: String = "",

    @SerializedName("language")
    val language: String = "",

    @SerializedName("description")
    val description: String = "",

    @SerializedName(value = "generatedContent", alternate = ["content"])
    val content: String = "",

    @SerializedName(value = "generatedAbstract", alternate = ["abstract"])
    val abstract: String = "",

    @SerializedName("wordCount")
    val wordCount: Int = 0,

    @SerializedName("isPublished")
    val isPublished: Boolean = false,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("userId")
    val userId: Long? = null
) {
    // Helper function برای نمایش کلمات کلیدی به صورت لیست
    fun getKeywordsList(): List<String> {
        return keywords
            .split("،", ",", "؛", ";", "ØŒ")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    // Helper function برای نمایش تاریخ به صورت خوانا
    fun getFormattedDate(): String {
        return createdAt?.take(10) ?: "" // فقط تاریخ بدون ساعت
    }
}
