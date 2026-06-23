package com.example.maghalam.model.net.api

import com.example.maghalam.model.repository.TokenInMemory
import com.example.maghalam.utills.SharedPreferencesManager
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenChecker(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val baseUrl: String
) : Authenticator {

    private val authApiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {

        if (responseCount(response) >= 2) {
            return null
        }

        if (response.request.url.encodedPath.contains("/api/auth/refresh")) {
            return null
        }

        val refreshToken = TokenInMemory.refreshToken ?: sharedPreferencesManager.getRefreshToken()
            ?: return null

        synchronized(this) {

            val success = refreshToken(refreshToken)

            if (!success) {
                TokenInMemory.clear()
                return null
            }

            return response.request.newBuilder()
                .header(
                    "Authorization",
                    "Bearer ${TokenInMemory.accessToken}"
                )
                .build()
        }
    }

    private fun refreshToken(
        refreshToken: String
    ): Boolean {

        return try {

            val response = authApiService
                .refreshToken(refreshToken)
                .execute()

            if (!response.isSuccessful) {
                return false
            }

            val body = response.body()
                ?: return false

            val accessToken = body.accessToken
                ?.takeIf { it.isNotBlank() }
                ?: return false

            TokenInMemory.saveToken(
                accessToken,
                body.refreshToken.orEmpty()
            )
            sharedPreferencesManager.saveTokens(
                accessToken,
                body.refreshToken.orEmpty()
            )

            true

        } catch (e: Exception) {
            false
        }
    }

    private fun responseCount(
        response: Response
    ): Int {

        var result = 1
        var current = response.priorResponse

        while (current != null) {
            result++
            current = current.priorResponse
        }

        return result
    }
}
