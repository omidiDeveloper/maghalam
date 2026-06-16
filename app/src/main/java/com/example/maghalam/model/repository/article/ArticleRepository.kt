package com.example.maghalam.model.repository.article


import com.example.maghalam.model.data.Article
import com.example.maghalam.model.net.dto.ApiResponse
import com.example.maghalam.model.net.dto.ArticleGenerationRequest
import java.io.File
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {


    fun getArticles():
            Flow<ApiResponse<List<Article>>>

    fun searchArticles(keyword: String):
            Flow<ApiResponse<List<Article>>>


    fun getArticleById(
        id: Long
    ): Flow<ApiResponse<Article>>


    fun deleteArticle(
        id: Long
    ): Flow<ApiResponse<Boolean>>


    fun insertArticle(
        article: Article
    ): Flow<ApiResponse<Boolean>>


    fun generateArticle(
        request: ArticleGenerationRequest
    ): Flow<ApiResponse<Article>>

    fun downloadArticle(
        article: Article,
        format: String
    ): Flow<ApiResponse<File>>
}
