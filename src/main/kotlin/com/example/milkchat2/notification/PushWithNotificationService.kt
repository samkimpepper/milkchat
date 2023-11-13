package com.example.milkchat2.notification

import com.example.milkchat2.infra.fcm.FcmPushClient
import org.springframework.stereotype.Service

@Service
class PushWithNotificationService(
        private val notificationService: NotificationService,
        private val fcmPushClient: FcmPushClient,
) {
}