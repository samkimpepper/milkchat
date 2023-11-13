package com.example.milkchat2.chat

import com.example.milkchat2.SecurityUtils
import com.example.milkchat2.chat.model.Chat
import com.example.milkchat2.chat.model.ChatStatus
import com.example.milkchat2.reservation.model.Reservation
import com.example.milkchat2.user.dto.UserInfo
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.*

@Service
class ChatSchedulerService(
        private val chatRepository: ChatRepository,
        private val taskScheduler: TaskScheduler,
        private val rSocketConnectionManager: RSocketConnectionManager,
) {

    suspend fun scheduleChatRoomOpening(reservation: Reservation):Unit = coroutineScope {
        val scheduledDateTime = reservation.reservationDate.atTime(reservation.reservationTime).atZone(ZoneId.of("Asia/Seoul")).toInstant()
        taskScheduler.schedule({
            launch { openChat(reservation) }
        }, Date.from(scheduledDateTime))
    }

    private suspend fun openChat(reservation: Reservation) {
        var users: MutableSet<UserInfo> = mutableSetOf(reservation.requester!!, reservation.accepter!!)

        var chat = Chat(
                users = users,
                status = ChatStatus.OPEN,
                expirationTime = reservation.reservationDate.atTime(reservation.reservationTime).plusMinutes(reservation.duration.minutes.toLong())
        )

        chatRepository.save(chat)

        val closeTimeIfNoJoin = Duration.ofMinutes(5)
        coroutineScope {
            taskScheduler.schedule({
                launch { checkAndCancelConnectionIfEmpty(chat) }
            }, Date.from(Instant.now().plus(closeTimeIfNoJoin)))
        }

        val closeTime = chat.expirationTime.atZone(ZoneId.of("Asia/Seoul")).toInstant()
        coroutineScope {
            taskScheduler.schedule({
                launch { closeChat(chat) }
            }, Date.from(closeTime))
        }
    }

    private suspend fun checkAndCancelConnectionIfEmpty(chat: Chat) {
        val connection = rSocketConnectionManager.getConnection(chat.id!!)
        if (connection == null || connection.size <= 1) {
            chat.status = ChatStatus.CANCELLED
            chatRepository.save(chat)
            rSocketConnectionManager.closeChatConnection(chat.id!!)
        }
    }

    private suspend fun closeChat(chat: Chat) {
        if (chat.status == ChatStatus.CANCELLED)
            return
        chat.status = ChatStatus.CLOSED
        chatRepository.save(chat)

        rSocketConnectionManager.closeChatConnection(chat.id!!)
    }

    suspend fun onConnect(requester: RSocketRequester, chatId: String) {
        val chat = chatRepository.findByStatusAndId(ChatStatus.OPEN, chatId)?: throw Exception("Chat not yet open")

        val email = SecurityUtils.currentUser().awaitFirst()
        val isUserInChat = chat.users.any { user -> user.email == email }
        if (!isUserInChat)
            throw Exception("User not part of the chat")

        rSocketConnectionManager.addConnection(chat.id!!, email, requester)
    }

    suspend fun sendMessage(chatId: String, message: String) {
        val connection = rSocketConnectionManager.getConnection(chatId)

        connection?.values?.forEach { requester ->
            requester.route("chat.message").data(message).send()
        }
    }

    suspend fun checkUserInChat(chatId: String): Chat {
        val chat = chatRepository.findById(chatId)?: throw Exception("Chat not found")
        val email = SecurityUtils.currentUser().awaitFirst()
        val isUserInChat = chat.users.any { user -> user.email == email }
        if (!isUserInChat)
            throw Exception("User not part of the chat")

        return chat
    }
}