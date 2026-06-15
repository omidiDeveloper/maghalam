package com.example.maghalam.model.net.dto

import com.example.maghalam.model.data.Article
import com.google.gson.annotations.SerializedName


data class ArticleGenerationRequest(
    val title: String,
    val author : String,
    val keywords: List<String>,
    val language: String = "fa",
    val description: String
)

//------------------------------------------------

data class ArticleResponse(
    @SerializedName("content")
    val content: List<Article>,

    @SerializedName("totalElements")
    val totalElements: Long,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("number")
    val currentPage: Int,

    @SerializedName("size")
    val pageSize: Int,

    @SerializedName("first")
    val isFirst: Boolean,

    @SerializedName("last")
    val isLast: Boolean,

    @SerializedName("empty")
    val isEmpty: Boolean
)

//------------------------------------------------


class ArticleDto {
}