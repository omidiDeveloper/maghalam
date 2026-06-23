package com.example.maghalam.model.net.api

import com.example.maghalam.model.data.Article
import com.example.maghalam.model.data.User
import com.example.maghalam.model.net.dto.ArticleGenerationRequest
import com.example.maghalam.model.net.dto.ArticleResponse
import com.example.maghalam.model.net.dto.AuthResponse
import com.example.maghalam.model.net.dto.LoginRequest
import com.example.maghalam.model.net.dto.RegisterRequest
import com.example.maghalam.model.net.dto.UserProfileResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ApiService {

    //Article Api Services =>
    @POST("api/articles/generate")
    suspend fun generateArticle(
        @Body request: ArticleGenerationRequest
    ): Response<Article>


    @GET("api/articles/{id}")
    suspend fun getArticleById(
        @Path("id") articleId: Long
    ): Response<Article>

    @DELETE("api/articles/{id}")
    suspend fun deleteArticle(
        @Path("id") id: Long
    ): Response<Boolean>

    @GET("api/articles")
    suspend fun getUserArticles(): Response<List<Article>>

    @POST("api/articles/{id}/publish")
    suspend fun publishArticle(
        @Path("id") articleId: Long
    ): Response<Article>

    @GET("api/articles/{id}/download")
    @Streaming
    suspend fun downloadArticle(
        @Path("id") articleId: Long,
        @Query("format") format: String // "html" or "pdf"
    ): Response<ResponseBody>

    @GET("articles/search")
    suspend fun searchArticles(
        @Query("keyword") keyword: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<ArticleResponse>

    @GET("api/getArticles")
    suspend fun getArticles(): Response<List<Article>>

    @POST("api/articles")
    suspend fun insertArticle(
        @Body
        article: Article
    ) : Response<Article>

    @GET("api/users")
    suspend fun getUsers(): Response<List<UserProfileResponse>>

    @GET("api/admin/users")
    suspend fun getAdminUsers(): Response<List<UserProfileResponse>>

    @DELETE("api/users/{id}")
    suspend fun deleteUserById(
        @Path("id") id: Long
    ): Response<Unit>

    //-----------------------------------------------------------------------------------

    //User Api Services =>
    @GET("api/users/profile")
    suspend fun getUserProfile(): Response<UserProfileResponse>

    @PUT("api/users/profile")
    suspend fun updateProfile(
        @Body request: Map<String, String>
    ): Response<UserProfileResponse>

    @PUT("api/users/settings")
    suspend fun updateSettings(
        @Body request: Map<String, Any>
    ): Response<UserProfileResponse>

    @PUT("api/users/password")
    suspend fun changePassword(
        @Body request: Map<String, String>
    ): Response<Unit>

    @DELETE("api/users/account")
    suspend fun deleteAccount(): Response<Unit>

    //-----------------------------------------------------------------------------------

    //Auth Api Service =>
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @GET("api/auth/refresh")
    fun refreshToken(@Query("refreshToken") refreshToken: String): Call<AuthResponse>


    @POST("api/auth/logout")
    suspend fun logout(): Response<Unit>

}
