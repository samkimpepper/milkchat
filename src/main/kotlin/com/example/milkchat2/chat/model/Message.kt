package com.example.milkchat2.chat.model

import org.springframework.data.annotation.Id

data class Message(
    @Id
    var id: String? = null,

    val chatId: String,

    val content: String,

    val sender: String,

    // TODO: createdAt
)
