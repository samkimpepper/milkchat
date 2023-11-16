package com.example.milkchat2.chat

import com.example.milkchat2.chat.model.Message
import com.mongodb.client.model.changestream.OperationType
import org.reactivestreams.Publisher
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class StreamMessageService(
    private val mongoTemplate: ReactiveMongoTemplate,
) {

    fun getNewMessagesForChat(chatId: String, email: String): Flux<Message?> {
        val messageIsForThisUserChat: (Message?) -> Publisher<Boolean> = { message ->
            Mono.just(chatId == message!!.chatId)
        }

        // SecurityContextHolder에서 이메일 가져와도 될 지 모르겠음
        return mongoTemplate.changeStream(Message::class.java)
                .watchCollection("messages")
                .listen()
                .doOnNext{ e -> println(e) }
                .filter { event -> event.operationType == OperationType.INSERT }
                .map{ it.body }
                .doOnNext {  }
                .filter { m -> m?.isNotFromSender(email) ?: false }
                .filterWhen(messageIsForThisUserChat)
    }

    private fun changeStream() {
        
    }
}