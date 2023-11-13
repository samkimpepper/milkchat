package com.example.milkchat2.infra.calendar

import com.example.milkchat2.SecurityUtils
import com.example.milkchat2.auth.OAuth2TokenService
import com.example.milkchat2.reservation.model.Reservation
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import java.time.format.DateTimeFormatter

@Service
class GoogleCalendarClient(
        @Value("\${google.calendar.list.request-url}")  private val calendarListRequestURL: String,
        @Value("\${google.calendar.add-event.request-url}") private val addEventRequestURL: String,
        private val oAuth2TokenService: OAuth2TokenService,
) {
    suspend fun getCalendarList() {
        val oidcUser = SecurityUtils.curretOAuth2UserInfo().awaitFirst()

        val accessToken = ""

        val webClient = WebClient.create()
        val response = webClient.get()
                .uri("$calendarListRequestURL?access_token=$accessToken")
                .retrieve()
                .bodyToMono(String::class.java)
                .awaitFirst()

        println(response)
//        val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken
//        val client = clientService.loadAuthorizedClient<OAuth2AuthorizedClient>(
//                authentication.authorizedClientRegistrationId,
//                authentication.name
//        )
//
//        val accessToken = client?.accessToken?.tokenValue
//        requireNotNull(accessToken) { "Access Token is required" }
//
//        val webClient = WebClient.create()
//        val response = webClient.get()
//                .uri("$calendarListRequestURL?access_token=$accessToken")
//                .retrieve()
//                .bodyToMono(String::class.java)
//                .awaitFirst()
//
//        println(response)
    }

    suspend fun addEvent(calendarId: String, reservation: Reservation, exchange: ServerWebExchange) {
        val oidcUser = SecurityUtils.curretOAuth2UserInfo().awaitFirst()
        val accessToken = oAuth2TokenService.getAccessToken(exchange).awaitFirst()

        val startLocalDateTime = reservation.reservationDate.atTime(reservation.reservationTime)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        val startDateTime = startLocalDateTime.format(formatter)
        val endDateTime = startLocalDateTime.plusMinutes(reservation.duration.minutes.toLong()).format(formatter)

        var addCalendarEventRequest = AddCalendarEventRequest(
            summary = "밀크챗 예정",
            description = "${reservation.requester?.nickname}님과 밀크챗 예약이 있습니다.",
            start = EventDateTime(startDateTime, "Asia/Seoul"),
            end = EventDateTime(endDateTime, "Asia/Seoul"),
        )

        val webClient = WebClient.create()
        val response = webClient.post()
                .uri("${addEventRequestURL}/${calendarId}/events")
                .header("Authorization", "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(addCalendarEventRequest)
                .retrieve()
                .bodyToMono(String::class.java)
                .awaitFirst()

        println(response)
    }

    suspend fun deleteEvent(calendarId: String, eventId: String, exchange: ServerWebExchange) {
        val accessToken = oAuth2TokenService.getAccessToken(exchange).awaitFirst()

        val webClient = WebClient.create()
        val response = webClient.delete()
                .uri("${addEventRequestURL}/${calendarId}/events/${eventId}")
                .header("Authorization", "Bearer $accessToken")
                .retrieve()
                .bodyToMono(String::class.java)
                .awaitFirst()

        println(response)
    }
}