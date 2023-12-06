package data.json.model

import app.service.StatusConverter

data class StatusData(val eventMap: MutableMap<String, BookEvent>) {

    val statusMap: MutableMap<String, PhoneStatusData> = mutableMapOf()

    init {
        statusMap.putAll(eventMap.mapValues { PhoneStatusData(it.value) })
    }

    data class BookEvent(val timestamp: String?, val linkedUserName: String?)

    data class PhoneStatusData(val event: BookEvent?) {
        val timestamp = event?.timestamp
        val linkedUserName = event?.linkedUserName
        val status: BookingStatus by lazy { StatusConverter.fromBooking(event) }
    }

    enum class BookingStatus {
        FREE, BOOKED_BY_THIS_USER, BOOKED_BY_ANOTHER_USER;
    }
}