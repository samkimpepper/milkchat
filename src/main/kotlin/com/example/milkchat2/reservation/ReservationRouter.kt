package com.example.milkchat2.reservation

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class ReservationRouter(private val reservationHandler: ReservationHandler) {

    @Bean
    fun reservationRoutes() = coRouter {
        "/api/reservation".nest {
            POST("/request", reservationHandler::request)
            PUT("/accept/{reservationId}", reservationHandler::accept)
            DELETE("/reject/{reservationId}", reservationHandler::reject)

        }
    }

    @Bean
    fun testRoutes() = coRouter {
        "/api/test/calendar".nest {
            GET("/list", reservationHandler::testGetCalendarList)
            GET("/create/event", reservationHandler::testAddCalendarEvent)
        }
    }
}