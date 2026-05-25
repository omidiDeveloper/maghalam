package com.example.maghalam.model


import com.example.maghalam.utills.SharedPreferencesManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sharedPreferencesManager: SharedPreferencesManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sharedPreferencesManager.getToken()

        val request = if (!token.isNullOrEmpty()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }
}
