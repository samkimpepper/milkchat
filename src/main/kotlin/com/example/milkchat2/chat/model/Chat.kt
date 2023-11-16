package com.example.milkchat2.chat.model

import com.example.milkchat2.user.dto.UserInfo
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class Chat(
    @Id
    var id: String? = null,

    var users: MutableSet<UserInfo> = mutableSetOf(),

    var status: ChatStatus,

    var expirationTime: LocalDateTime,
)
