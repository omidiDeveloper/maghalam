package com.example.maghalam.model

import com.example.maghalam.model.net.api.ArticleApiService
import com.example.maghalam.model.net.api.AuthApiService
import com.example.maghalam.model.net.api.UserApiService
import com.example.maghalam.utills.SharedPreferencesManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://127.0.0.1:3306/" //

    fun create(sharedPreferencesManager: SharedPreferencesManager): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sharedPreferencesManager))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun createAuthApi(sharedPreferencesManager: SharedPreferencesManager): AuthApiService =
        create(sharedPreferencesManager).create(AuthApiService::class.java)

    fun createUserApi(sharedPreferencesManager: SharedPreferencesManager): UserApiService =
        create(sharedPreferencesManager).create(UserApiService::class.java)

    fun createArticleApi(sharedPreferencesManager: SharedPreferencesManager): ArticleApiService =
        create(sharedPreferencesManager).create(ArticleApiService::class.java)
}