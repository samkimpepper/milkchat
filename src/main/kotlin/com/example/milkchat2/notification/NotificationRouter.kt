package com.example.milkchat2.notification

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class NotificationRouter(
        private val notificationHandler: NotificationHandler,
) {
    @Bean
    fun testNotificationRoutes() = coRouter {
        "/".nest {
            GET("api/test/fcm", notificationHandler::getFcmTestPage)
            GET("firebase-messaging-sw.js", notificationHandler::getFcm)

        }
    }
}