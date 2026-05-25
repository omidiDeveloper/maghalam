package com.example.maghalam

import android.app.Application
import com.example.maghalam.model.repository.TokenInMemory
import com.example.maghalam.utills.SharedPreferencesManager

class MyApplication : Application() {

    lateinit var sharedPreferencesManager: SharedPreferencesManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // مقداردهی SharedPreferences
        sharedPreferencesManager = SharedPreferencesManager(this)

        // بارگذاری token از SharedPreferences به TokenInMemory
        val savedToken = sharedPreferencesManager.getToken()
        if (!savedToken.isNullOrEmpty()) {
            TokenInMemory.saveToken(savedToken)

            val username = sharedPreferencesManager.getUsername()
            val userId = sharedPreferencesManager.getUserId()
            if (username != null && userId != -1L) {
                TokenInMemory.saveUserInfo(username, userId)
            }
        }
    }

    companion object {
        lateinit var instance: MyApplication
            private set
    }
}
