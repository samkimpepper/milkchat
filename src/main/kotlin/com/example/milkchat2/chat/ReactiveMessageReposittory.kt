package com.example.milkchat2.chat

import com.example.milkchat2.chat.model.Message
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ReactiveMessageReposittory: ReactiveMongoRepository<Message, String> {

    fun findAllByChatId(chatId: String): Flux<Message>
}