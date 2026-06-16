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

    var role: String = "USER"
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
        userId : Long,
        role: String = "USER"
    ){
        this.username = username
        this.userId = userId
        this.role = role
    }


    fun clear() {
        accessToken = null
        refreshToken = null
        username = null
        userId = null
        role = "USER"
    }
}
