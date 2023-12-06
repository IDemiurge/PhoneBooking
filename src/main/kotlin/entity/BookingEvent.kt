package entity

import java.time.Instant

data class BookingEvent(val timestamp: Instant, val name: String, val operation: BookingOperation) {

    enum class BookingOperation{
        BOOKED,
        RELEASED,
    }
}