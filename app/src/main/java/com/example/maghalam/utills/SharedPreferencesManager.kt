package com.example.maghalam.utills

import android.content.Context
import android.content.SharedPreferences
import com.example.maghalam.model.repository.TokenInMemory

class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "maghalam_prefs"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_FONT_SIZE = "font_size"
    }

    // Token Management
    fun saveToken(token: String , refreshToken: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
        TokenInMemory.saveToken(token ,refreshToken )
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun clearToken() {
        sharedPreferences.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
        TokenInMemory.clear()
    }

    // Refresh Token Management
    fun saveRefreshToken(refreshToken: String) {
        sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit()
            .putString(KEY_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
        TokenInMemory.saveToken(accessToken , refreshToken)
    }

    // User Info
    fun saveUserInfo(username: String, userId: Long) {
        sharedPreferences.edit()
            .putString(KEY_USERNAME, username)
            .putLong(KEY_USER_ID, userId)
            .apply()
        TokenInMemory.saveUserInfo(username, userId )
    }

    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }

    fun getUserId(): Long {
        return sharedPreferences.getLong(KEY_USER_ID, -1L)
    }

    // Appearance Settings
    fun saveDarkMode(isDarkMode: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_DARK_MODE, isDarkMode).apply()
    }

    fun getDarkMode(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false)
    }


    fun getFontSize(): String {
        return sharedPreferences.getString(KEY_FONT_SIZE, "Vazir") ?: "Vazir"
    }

    fun saveFontSize(fontSize: String) {
        sharedPreferences.edit().putString(KEY_FONT_SIZE, fontSize).apply()
    }


    // Clear All Data (Logout)
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
        TokenInMemory.clear()
    }

    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }
}
