package com.example.maghalam.model.net.dto.request

data class ArticleGenerationRequest(
    val topic: String,
    val keywords: List<String>? = null,
    val length: String = "medium", // short, medium, long
    val language: String = "fa"
)