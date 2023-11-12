package com.example.milkchat2.notification

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : CoroutineCrudRepository<Notification, String> {
}