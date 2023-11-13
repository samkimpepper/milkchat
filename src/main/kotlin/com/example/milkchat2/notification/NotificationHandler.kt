package com.example.milkchat2.notification

import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.renderAndAwait

@Component
class NotificationHandler(
    private val notificationService: NotificationService,
) {

    suspend fun getFcmTestPage(serverRequest: ServerRequest): ServerResponse {
        return ServerResponse.ok()
                .renderAndAwait("notification-test.html")
    }

    suspend fun getFcm(serverRequest: ServerRequest): ServerResponse {
        val resource = ClassPathResource("firebase-messaging-sw.js")
        return ServerResponse.ok().contentType(MediaType.valueOf("application/javascript"))
                .bodyValueAndAwait(resource)
    }
}