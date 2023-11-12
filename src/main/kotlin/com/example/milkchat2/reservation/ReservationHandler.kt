package com.example.milkchat2.reservation

import com.example.milkchat2.SecurityUtils
import com.example.milkchat2.infra.calendar.GoogleCalendarClient
import com.example.milkchat2.reservation.dto.RequestRequest
import com.example.milkchat2.user.UserRepository
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.buildAndAwait


@Component
class ReservationHandler(
        private val reservationService: ReservationService,
        private val userRepository: UserRepository,
        private val googleCalendarClient: GoogleCalendarClient,
) {

    suspend fun request(serverRequest: ServerRequest): ServerResponse {
        val requestRequest = serverRequest.awaitBody<RequestRequest>()
        val email = SecurityUtils.currentUser().awaitFirst()
        val sender = userRepository.findByEmail(email)
        val receiver = userRepository.findById(requestRequest.receiverId)?: throw Exception("User not found")

        reservationService.requestReservation(sender, receiver, requestRequest)

        return ServerResponse.ok().buildAndAwait()

    }

    suspend fun accept(serverRequest: ServerRequest): ServerResponse {
        val reservationId = serverRequest.pathVariable("reservationId")

        reservationService.acceptReservation(reservationId, serverRequest.exchange())

        return ServerResponse.ok().buildAndAwait()
    }

    suspend fun reject(serverRequest: ServerRequest): ServerResponse {
        val reservationId = serverRequest.pathVariable("reservationId")

        reservationService.rejectReservation(reservationId)

        return ServerResponse.ok().buildAndAwait()
    }

    suspend fun testGetCalendarList(serverRequest: ServerRequest): ServerResponse {
        googleCalendarClient.getCalendarList()

        return ServerResponse.ok().buildAndAwait()
    }

    suspend fun testAddCalendarEvent(serverRequest: ServerRequest): ServerResponse {
        val sender = userRepository.findByEmail(SecurityUtils.currentUser().awaitFirst())
        val receiver = userRepository.findByEmail("testuser@gmail.com") ?: throw Exception("User not found")

        reservationService.createReservation(sender, receiver, serverRequest.exchange())

        return ServerResponse.ok().buildAndAwait()
    }
}