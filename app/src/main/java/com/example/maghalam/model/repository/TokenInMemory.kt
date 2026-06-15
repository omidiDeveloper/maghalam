package com.example.maghalam.model.repository


object TokenInMemory {

    var accessToken: String? = null
        private set

    var refreshToken: String? = null
        private set

    var username: String? = null
        private set

    var userId: Long? = null
        private set

    fun saveToken(
        accessToken: String,
        refreshToken: String
    ) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    fun saveUserInfo(
        username: String,
        userId : Long
    ){
        this.username = username
        this.userId = userId
    }


    fun clear() {
        accessToken = null
        refreshToken = null
        username = null
        userId = null
    }
}
