package com.example.milkchat2.infra.calendar

data class AddCalendarEventRequest(
        val summary: String,
        val description: String,
        val start: EventDateTime,
        val end: EventDateTime,
)

data class EventDateTime(
    val dateTime: String,
    val timeZone: String,
)