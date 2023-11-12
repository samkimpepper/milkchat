package com.example.milkchat2.user.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document("users")
data class User(
    @Id
    val id: String? = null,

    @Indexed(unique=true)
    var email: String?,

    var nickname: String?,

    var password: String? = null,

    var isAdmin: Boolean = false,

    var favoredUsers: MutableList<String> = mutableListOf(),

    var isActive: Boolean = true,

    var lastLoginTimestamp: Long = System.currentTimeMillis(),

    var careerStage: CareerStage? = null,

    var jobField: JobField? = null,

    var fcmToken: String? = null,

    var provider: String? = null,
) {
}