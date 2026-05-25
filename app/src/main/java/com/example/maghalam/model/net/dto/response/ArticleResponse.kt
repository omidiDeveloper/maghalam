package com.example.maghalam.model.net.dto.response

import com.example.maghalam.model.data.Article
import com.google.gson.annotations.SerializedName

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
