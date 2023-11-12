package com.example.milkchat2.notification

import com.example.milkchat2.user.model.User
import org.springframework.stereotype.Service


interface NotificationService {
    suspend fun send(sender: User, receiver: String, type: NotificationType, link: String?)
}

@Service
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository,
) : NotificationService {
    override suspend fun send(sender: User, receiver: String, type: NotificationType, link: String?) {
        var title: String? = null
        var content: String? = null
        if (type == NotificationType.CHAT_REQUEST) {
            title = "밀크챗 요청"
            content = "${sender.nickname}님께서 밀크챗을 요청하셨습니다. 수락하시겠습니까?"
        }
        else if (type == NotificationType.CHAT_ACCEPT) {
            title = "밀크챗 수락"
            content = "${sender.nickname}님께서 밀크챗 요청을 수락하셨습니다."
        }
        else if(type == NotificationType.CHAT_REJECT) {
            title = "밀크챗 거절"
            content = "${sender.nickname}님께서 밀크챗 요청을 거절하셨습니다."
        }

        val notification = Notification(
                senderId = sender.id,
                receiverId = receiver,
                title = title!!,
                content = content!!,
                type = type,
                link = link,
        )

        notificationRepository.save(notification)
    }
}