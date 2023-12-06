package data

import data.json.StatusData
import java.time.Instant

class Data {
    companion object {
        lateinit var statusData: StatusData  //empty?
    }

    data class BookEvent(val timestamp: Instant?, val linkedUserName: String?)

    data class PhoneStatusData(val event: BookEvent?) {
        val timestamp = event?.timestamp
        val linkedUserName = event?.linkedUserName
        var status: BookingStatus = BookingStatus.FREE

        init {
            linkedUserName?.let {
                if (Main.userName.equals(linkedUserName))
                    status = BookingStatus.BOOKED_BY_THIS_USER
                else
                    status = BookingStatus.BOOKED_BY_ANOTHER_USER
            }

        }
    }

    //    data class Band(
//        val type: BandType,
//        val frequency: String
//    )
//
    enum class BookingStatus {
        FREE, BOOKED_BY_THIS_USER, BOOKED_BY_ANOTHER_USER;
    }
}