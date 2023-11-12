package com.example.milkchat2.reservation.dto

import com.example.milkchat2.reservation.model.Reservation
import com.example.milkchat2.reservation.model.UserInfo
import com.example.milkchat2.user.model.User
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class RequestRequest(
    val receiverId: String,
    val reservationDate: String,
    val reservationTime: String,
    val duration: Int,
) {

}