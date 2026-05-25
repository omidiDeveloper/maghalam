package com.example.maghalam.model.net.api


import com.example.maghalam.model.data.Article
import com.example.maghalam.model.net.dto.request.ArticleGenerationRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ArticleApiService {

    @POST("api/articles/generate")
    suspend fun generateArticle(
        @Body request: ArticleGenerationRequest
    ): Response<Article>

    @POST("api/articles")
    suspend fun createArticle(
        @Body request: HashMap<String, String>
    ): Response<Article>


    @GET("api/articles/{id}")
    suspend fun getArticleById(
        @Path("id") articleId: Long
    ): Response<Article>

    @GET("api/articles")
    suspend fun getUserArticles(): Response<List<Article>>

    @POST("api/articles/{id}/publish")
    suspend fun publishArticle(
        @Path("id") articleId: Long
    ): Response<Article>

    @GET("api/articles/{id}/download")
    suspend fun downloadArticle(
        @Path("id") articleId: Long,
        @Query("format") format: String // "html" or "pdf"
    ): Response<ResponseBody>

    @GET("articles/search")
    suspend fun searchArticles(
        @Query("keyword") keyword: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<List<Article>>

    @GET("api/articles")
    suspend fun getArticles(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<List<Article>>


}
