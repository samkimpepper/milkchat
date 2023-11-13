package com.example.milkchat2.chat

import org.slf4j.LoggerFactory
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class RSocketConnectionManager {
    private val chatConnections = ConcurrentHashMap<String, ConcurrentHashMap<String, RSocketRequester>>()
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun addConnection(chatId: String, email: String, requester: RSocketRequester) {
        chatConnections.computeIfAbsent(chatId) {
            ConcurrentHashMap()
        }[email] = requester

        requester.rsocket()
                ?.onClose()
                ?.doFirst {
                    log.info("접속")
                }
                ?.doOnError { error ->
                    log.error(error.toString())
                }
                ?.doFinally {
                    removeConnection(chatId, email)
                    log.info("접속 종료")
                }
                ?.subscribe()
    }

    suspend fun getConnection(chatId: String): Map<String, RSocketRequester>? {
        return chatConnections[chatId]
    }

    fun removeConnection(chatId: String, email: String) {
        chatConnections[chatId]?.remove(email)
    }

    suspend fun closeChatConnection(chatId: String) {
        chatConnections.remove(chatId)
    }
}