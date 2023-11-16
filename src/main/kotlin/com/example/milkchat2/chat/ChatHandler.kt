package com.example.milkchat2.chat

import com.example.milkchat2.SecurityUtils
import com.example.milkchat2.auth.CurrentUser
import com.example.milkchat2.chat.dto.ConnectChatRequest
import com.example.milkchat2.chat.dto.SendMessageRequest
import com.example.milkchat2.chat.dto.StreamMessageRequest
import com.example.milkchat2.chat.model.Message
import com.example.milkchat2.user.UserRepository
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Controller
class ChatHandler(
        private val chatService: ChatService,
        private val chatSchedulerService: ChatSchedulerService,
        private val messageService: MessageService,
        private val userRepository: UserRepository,
        private val streamMessageService: StreamMessageService,
) {

    @ConnectMapping("/chat")
    suspend fun onConnect(
                            requester: RSocketRequester,
                          @Payload chatId: String) {
        println("onConnect")

        chatSchedulerService.onConnect(requester, chatId)
    }

    @ConnectMapping
    suspend fun testOnConnect(@AuthenticationPrincipal jwtMono: Mono<Jwt>,
                          requester: RSocketRequester,
                          @Payload request: ConnectChatRequest) {
        println("onConnect")
        val email = jwtMono.awaitSingle().getClaimAsString("sub")
        val testuser = userRepository.findByEmail(email)
        val testuser2 = userRepository.findByEmail("testuser2@gmail.com")
        //chatSchedulerService.openChatForTest(testuser, testuser2)
        val chatId = request.chatId

        println("chatId: $chatId")

        chatSchedulerService.onConnectForTest(requester, email, chatId)
    }

    @MessageMapping("message.incoming")
    suspend fun handleMessage(request: SendMessageRequest,
                              @AuthenticationPrincipal jwtMono: Mono<Jwt>) {
        println("handle message")
        //println("jwtMono: ${jwtMono}")
        val chatId = request.chatId
        val message = request.message
        val email = jwtMono.awaitSingle().getClaimAsString("sub")

        chatSchedulerService.sendMessage(chatId, message)
        messageService.saveMessage(chatId, email, message)
    }

    @MessageMapping("message.outgoing")
    fun streamMessage(request: StreamMessageRequest,
            @AuthenticationPrincipal jwtMono: Mono<Jwt>): Flux<Message?> {
        return jwtMono
                .flatMap { jwt -> jwt.getClaimAsString("sub").toMono() }
                .flatMapMany { email ->
                    val chatId = request.chatId
                    streamMessageService.getNewMessagesForChat(chatId, email)
                }
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