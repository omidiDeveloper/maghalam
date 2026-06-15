package com.example.maghalam.model.net.api

import com.example.maghalam.model.repository.TokenInMemory
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TokenChecker : Authenticator, KoinComponent {

    private val authApiService: ApiService by inject()

    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {

        if (responseCount(response) >= 2) {
            return null
        }

        val refreshToken = TokenInMemory.refreshToken
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

            TokenInMemory.saveToken(
                body.accessToken,
                body.refreshToken
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