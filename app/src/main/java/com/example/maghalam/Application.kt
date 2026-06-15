package com.example.maghalam

import android.app.Application
import com.example.maghalam.di.appModule
import com.example.maghalam.model.repository.TokenInMemory
import com.example.maghalam.utills.SharedPreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class MyApplication : Application() {


    override fun onCreate() {
        super.onCreate()


        startKoin {

            androidContext(this@MyApplication)

            modules(
                appModule
            )
        }


        // Load token after Koin is ready
        loadToken()

    }



    private fun loadToken() {

        val preferences =
            SharedPreferencesManager(this)


        val savedToken =
            preferences.getToken()


        if (!savedToken.isNullOrEmpty()) {

            TokenInMemory.saveToken(
                savedToken,
                preferences.getRefreshToken() ?: ""
            )


            val username =
                preferences.getUsername()


            val userId =
                preferences.getUserId()


            if(username != null && userId != -1L){

                TokenInMemory.saveUserInfo(
                    username,
                    userId
                )
            }
        }
    }
}