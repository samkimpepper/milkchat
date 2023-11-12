package com.example.milkchat2.notification

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.stereotype.Component

enum class NotificationType(
    val value: Int,
) {
    NORMAL(0),
    CHAT_REQUEST(1),
    CHAT_ACCEPT(2),
    CHAT_REJECT(3);

    companion object {
        private val valueMap = NotificationType.values().associateBy { e -> e.value }

        fun getOfValue(value: Int): NotificationType? = valueMap[value]
    }
}

@ReadingConverter
@Component
class NotificationTypeReadConverter: Converter<Int, NotificationType> {
    override fun convert(source: Int): NotificationType= NotificationType.getOfValue(source)!!

}

@Component
@WritingConverter
class NotificationTypeWriteConverter: Converter<NotificationType, Int> {
    override fun convert(source: NotificationType): Int = source.value
}