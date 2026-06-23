package com.example.maghalam.utills

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.example.maghalam.model.repository.TokenInMemory
import com.google.gson.JsonElement
import com.google.gson.JsonParser

class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "maghalam_prefs"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USERNAME = "username"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_EMAIL = "email"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_INTRO_SEEN = "intro_seen"
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

    // Keeps the current session available after process restarts.
    fun saveUserInfo(
        username: String,
        userId: Long,
        role: String = "USER",
        fullName: String = "",
        email: String = ""
    ) {
        sharedPreferences.edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_FULL_NAME, fullName)
            .putString(KEY_EMAIL, email)
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_USER_ROLE, role)
            .apply()
        TokenInMemory.saveUserInfo(username, userId, role)
    }

    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }

    fun getFullName(): String {
        return sharedPreferences.getString(KEY_FULL_NAME, "") ?: ""
    }

    fun getEmail(): String {
        return sharedPreferences.getString(KEY_EMAIL, "") ?: ""
    }

    fun getUserId(): Long {
        return sharedPreferences.getLong(KEY_USER_ID, -1L)
    }

    fun getUserRole(): String {
        return sharedPreferences.getString(KEY_USER_ROLE, "USER") ?: "USER"
    }

    fun isAdmin(): Boolean {
        return getUserRole().isAdminRole() || tokenHasAdminRole()
    }

    private fun tokenHasAdminRole(): Boolean {
        val token = getToken().orEmpty()
        if (token.isBlank()) return false

        return runCatching {
            val payload = token.split(".").getOrNull(1) ?: return@runCatching false
            val decoded = String(Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP))
            val json = JsonParser.parseString(decoded).asJsonObject

            json.containsAdminRole()
        }.getOrDefault(false)
    }

    private fun JsonElement?.containsAdminRole(): Boolean {
        if (this == null || isJsonNull) return false

        return when {
            isJsonPrimitive -> asString.isAdminRole()
            isJsonArray -> asJsonArray.any { it.containsAdminRole() }
            isJsonObject -> asJsonObject.entrySet().any { it.value.containsAdminRole() }
            else -> false
        }
    }

    private fun String.isAdminRole(): Boolean {
        return split(',', ' ', ';')
            .any { value ->
                val normalized = value.trim().removePrefix("ROLE_")
                normalized.equals("ADMIN", ignoreCase = true) ||
                        normalized.equals("SUPER_ADMIN", ignoreCase = true)
            } || contains("ADMIN", ignoreCase = true)
    }

    fun hasSeenIntro(): Boolean {
        return sharedPreferences.getBoolean(KEY_INTRO_SEEN, false)
    }

    fun markIntroSeen() {
        sharedPreferences.edit().putBoolean(KEY_INTRO_SEEN, true).apply()
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
