package com.example.milkchat2.reservation

import com.example.milkchat2.reservation.model.Reservation
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReservationRepository : CoroutineCrudRepository<Reservation, String> {
}