package com.example.milkchat2.chat

import com.example.milkchat2.chat.model.Message
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MessageRepository: CoroutineCrudRepository<Message, String> {

    suspend fun findAllByChatId(chatId: String): Flow<Message>

    @Query("SELECT m FROM Message m WHERE m.chatId = :chatId ORDER BY m.createdAt DESC")
    suspend fun findAllByChatIdWithPaging(
            chatId: String,
            pageable: Pageable
    ): Flow<Message>
}