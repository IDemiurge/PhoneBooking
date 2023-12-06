package app.service

import data.json.model.StatusData

class StatusConverter {
    companion object {
        lateinit var userName: String

        fun fromBooking(event: StatusData.BookEvent?): StatusData.BookingStatus {
            event?.linkedUserName?.let {
                if (userName.equals(it))
                    return StatusData.BookingStatus.BOOKED_BY_THIS_USER
                else
                    return StatusData.BookingStatus.BOOKED_BY_ANOTHER_USER
            }
            return StatusData.BookingStatus.FREE
        }

        fun convert(newData: MutableMap<String, StatusData.BookEvent>): Map<out String, StatusData.PhoneStatusData> {
            return newData.mapValues { StatusData.PhoneStatusData(it.value) }
        }

        fun convertBack(statusMap: MutableMap<String, StatusData.PhoneStatusData>): MutableMap<String, StatusData.BookEvent> {
            val map: MutableMap<String, StatusData.BookEvent> = mutableMapOf()
            map.putAll(statusMap.mapValues {
                StatusData.BookEvent(
                    it.value.timestamp,
                    it.value.linkedUserName
                )
            })
            return map
        }
    }
}