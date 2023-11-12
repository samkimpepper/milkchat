package com.example.milkchat2.reservation.model

enum class Duration(val minutes: Int) {
    TWENTY_MINUTES(20),
    THIRTY_MINUTES(30),
    FORTY_MINUTES(40);

    companion object {
        fun fromMinutes(minutes: Int): Duration? {
            return values().firstOrNull { it.minutes == minutes }
        }
    }
}
