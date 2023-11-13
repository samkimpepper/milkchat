package com.example.milkchat2.chat

import com.example.milkchat2.chat.model.Chat
import com.example.milkchat2.chat.model.ChatStatus
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ChatRepository: CoroutineCrudRepository<Chat, String> {
    suspend fun findByStatusAndId(status: ChatStatus, id: String): Chat?
}