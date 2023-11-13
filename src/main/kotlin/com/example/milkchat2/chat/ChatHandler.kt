package com.example.milkchat2.chat

import com.example.milkchat2.SecurityUtils
import com.example.milkchat2.chat.dto.SendMessageRequest
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class ChatHandler(
        private val chatService: ChatService,
        private val chatSchedulerService: ChatSchedulerService,
        private val messageService: MessageService,
) {

    @ConnectMapping("/chat")
    suspend fun onConnect(requester: RSocketRequester, @Payload chatId: String) {
        println("onConnect")
        chatSchedulerService.onConnect(requester, chatId)
    }

    @MessageMapping
    suspend fun handleMessage(request: SendMessageRequest) {
        val chatId = request.chatId
        val message = request.message

        chatSchedulerService.sendMessage(chatId, message)
        messageService.saveMessage(chatId, SecurityUtils.currentUser().awaitSingle(), message)
    }

    @GetMapping("/api/chat/{chatId}/messages")
    suspend fun getMessagesByChat(@PathVariable chatId: String) {
        val chat = chatSchedulerService.checkUserInChat(chatId)
        // messageService.getAllMessages()
    }

    @GetMapping("/api/test/chat")
    suspend fun getTestChatPage(): String {

        return "chat-test"
    }
}