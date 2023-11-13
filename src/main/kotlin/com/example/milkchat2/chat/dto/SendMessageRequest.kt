package com.example.milkchat2.chat.dto

data class SendMessageRequest(
    val chatId: String,
    val message: String,
)
