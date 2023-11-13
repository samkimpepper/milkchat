package com.example.milkchat2.reservation

import com.example.milkchat2.infra.calendar.GoogleCalendarClient
import com.example.milkchat2.notification.NotificationService
import com.example.milkchat2.notification.NotificationType
import com.example.milkchat2.reservation.dto.RequestRequest
import com.example.milkchat2.reservation.model.Duration
import com.example.milkchat2.reservation.model.Reservation
import com.example.milkchat2.reservation.model.ReservationStatus
import com.example.milkchat2.user.UserRepository
import com.example.milkchat2.user.dto.UserInfo
import com.example.milkchat2.user.model.User
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


interface ReservationService {
    suspend fun requestReservation(sender: User, receiver: User, requestRequest: RequestRequest)
    suspend fun acceptReservation(reservationId: String, exchange: ServerWebExchange)
    suspend fun rejectReservation(reservationId: String, exchange: ServerWebExchange)
    suspend fun createReservation(sender: User, receiver: User, exchange: ServerWebExchange)
}

@Service
class ReservationServiceImpl(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val notificationService: NotificationService,
    private val googleCalendarClient: GoogleCalendarClient,
) : ReservationService {

    override suspend fun requestReservation(sender: User, receiver: User, requestRequest: RequestRequest) {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        val duration = Duration.fromMinutes(requestRequest.duration) ?: throw Exception("Invalid duration value")

        val reservation = Reservation(
                requester = UserInfo(sender.id!!, sender.nickname!!, sender.email!!),
                accepter = UserInfo(receiver.id!!, receiver.nickname!!, receiver.email!!),
                reservationDate = LocalDate.parse(requestRequest.reservationDate, dateFormatter),
                reservationTime = LocalTime.parse(requestRequest.reservationTime, timeFormatter),
                duration = duration,
        )

        reservationRepository.save(reservation)

        val link = "/api/reservation/accept/${reservation.id}"
        notificationService.send(sender, requestRequest.receiverId, NotificationType.CHAT_REQUEST, link)
    }

    override suspend fun acceptReservation(reservationId: String, exchange: ServerWebExchange) {
        var reservation = reservationRepository.findById(reservationId) ?: throw Exception("Reservation not found")
        reservation.status = ReservationStatus.CONFIRMED
        reservationRepository.save(reservation)

        val accepter = reservation.accepter?.let { userRepository.findById(it.id) }
        val requester = reservation.requester?.let { userRepository.findById(it.id) }

        reservation.requester?.let { notificationService.send(accepter!!, it.id, NotificationType.CHAT_ACCEPT, "") }

        if (requester != null && requester.provider == "google") {
            googleCalendarClient.addEvent(requester.email!!, reservation, exchange)
        }
        if (accepter != null && accepter.provider == "google") {
            googleCalendarClient.addEvent(accepter.email!!, reservation, exchange)
        }
    }

    override suspend fun rejectReservation(reservationId: String, exchange: ServerWebExchange) {
        var reservation = reservationRepository.findById(reservationId) ?: throw Exception("Reservation not found")

        val sender = reservation.accepter?.let { userRepository.findById(it.id) }

        reservation.requester?.let { notificationService.send(sender!!, it.id, NotificationType.CHAT_REJECT, "") }

        if (sender != null && sender.provider == "google") {
            googleCalendarClient.deleteEvent(sender.email!!, reservation.googleCalendarEventId!!, exchange)
        }


        reservationRepository.deleteById(reservationId)
    }

    override suspend fun createReservation(sender: User, receiver: User, exchange: ServerWebExchange) {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

        val duration = Duration.fromMinutes(30) ?: throw Exception("Invalid duration value")

        val reservation = Reservation(
                requester = UserInfo(sender.id!!, sender.email!!, sender.email!!),
                accepter = UserInfo(receiver.id!!, receiver.nickname!!, receiver.email!!),
                reservationDate = LocalDate.parse("2023-11-12", dateFormatter),
                reservationTime = LocalTime.parse("10:00:00", timeFormatter),
                duration = duration,
        )

        reservationRepository.save(reservation)

        googleCalendarClient.addEvent(sender.email!!, reservation, exchange)
    }
}