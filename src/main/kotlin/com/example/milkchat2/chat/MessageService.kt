package com.example.milkchat2.chat

import com.example.milkchat2.chat.model.Message
import org.springframework.stereotype.Service

@Service
class MessageService(
        private val messageRepository: MessageRepository,
) {

    suspend fun saveMessage(chatId: String, senderEmail: String, content: String) {
        val message = Message(
            chatId = chatId,
            content = content,
            sender = senderEmail,
        )

        messageRepository.save(message)
    }

    suspend fun getAllMessages(chatId: String) {

    }
}