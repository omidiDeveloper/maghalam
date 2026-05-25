package com.example.maghalam.model.repository


object TokenInMemory {
    var token: String? = null
        private set

    var username: String? = null
        private set

    var userId: Long? = null
        private set

    fun saveToken(newToken: String) {
        token = newToken
    }

    fun saveUserInfo(newUsername: String, newUserId: Long) {
        username = newUsername
        userId = newUserId
    }

    fun clearToken() {
        token = null
        username = null
        userId = null
    }

    fun isTokenAvailable(): Boolean {
        return !token.isNullOrEmpty()
    }
}
