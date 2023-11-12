package com.example.milkchat2.reservation.model

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

    var accepter: UserInfo?
)

data class UserInfo(
    val id: String,
    val nickname: String,
    val email: String,
)