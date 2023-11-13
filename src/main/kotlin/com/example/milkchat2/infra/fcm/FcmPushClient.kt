package com.example.milkchat2.infra.fcm

import com.example.milkchat2.user.model.User
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service

@Service
class FcmPushClient(
        @Value("\${fcm.certification}") private val credential: String,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    init {
        val resource = ClassPathResource(credential)

        try {
            resource.inputStream.use { stream ->
                val options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(stream))
                        .build()

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options)
                    log.info("Firebase init success!")
                }
            }
        } catch (e: Exception) {
            log.error("Firebase init failed")
        }


    }

    suspend fun sendMessage(user: User, title: String, body: String) {
        if (user.fcmToken == null) {
            log.error("fcm token not found")
            return
        }

        val notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build()

        val msg = Message.builder()
                .setToken(user.fcmToken)
                .setNotification(notification)
                .build()

        val response = FirebaseMessaging.getInstance().send(msg)
        log.info(response)


    }
}