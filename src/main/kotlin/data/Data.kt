package data

import data.json.StatusData
import java.time.Instant

class Data {
    companion object {
        lateinit var statusData: StatusData  //empty?
    }

    data class BookEvent(val timestamp: Instant?, val linkedUserName: String?)

    data class PhoneStatus(val event: BookEvent) {
        val timestamp = event.timestamp
        val linkedUserName = event.linkedUserName
        var status: BookingStatus

        init {
            if (linkedUserName.isNullOrEmpty())
                status = BookingStatus.FREE
            else if (Main.userName.equals(linkedUserName))
                status = BookingStatus.BOOKED_BY_THIS_USER
            else status = BookingStatus.BOOKED_BY_ANOTHER_USER
        }
    }

    data class PhoneModel(
        val modelName: String,
        val bands2g: List<String>,
        val bands3g: List<String>,
        val bands4g: List<String>
    )

    //    data class Band(
//        val type: BandType,
//        val frequency: String
//    )
//
    enum class BookingStatus {
        FREE, BOOKED_BY_THIS_USER, BOOKED_BY_ANOTHER_USER
    }
}