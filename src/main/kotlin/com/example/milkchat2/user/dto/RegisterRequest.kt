package com.example.milkchat2.user.dto

import com.example.milkchat2.user.model.User

data class RegisterRequest(
    val email: String,
    val nickname: String?,
    val password: String,
){
    fun toUser(): User {
        return User(
            email = email,
            nickname = nickname,
            password = password,
        )
    }
}
