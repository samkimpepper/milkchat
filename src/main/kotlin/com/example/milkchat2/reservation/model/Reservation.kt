package com.example.milkchat2.reservation.model

import com.example.milkchat2.user.dto.UserInfo
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalTime

@Document
data class Reservation(
        @Id
    val id: String? = null,

        var reservationDate: LocalDate,

        var reservationTime: LocalTime,

        var duration: Duration,

        var status: ReservationStatus = ReservationStatus.PENDING,

        var requester: UserInfo?,

        var accepter: UserInfo?,

        var googleCalendarEventId: String? = null,
)
