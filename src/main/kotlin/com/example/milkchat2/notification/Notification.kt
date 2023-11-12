package com.example.milkchat2.notification

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.IndexDirection
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.mapping.FieldType
import java.time.LocalDateTime

@Document
data class Notification(
    @Id
    val id: String? = null,

    @Indexed
    @Field("sender_id", targetType= FieldType.OBJECT_ID)
    val senderId: String?,

    @Indexed
    @Field("receiver_id", targetType = FieldType.OBJECT_ID)
    val receiverId: String?,

    val title: String,

    val content: String,

    val link: String?,

    val type: NotificationType,

    @Indexed(direction = IndexDirection.DESCENDING)
    @Field("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
