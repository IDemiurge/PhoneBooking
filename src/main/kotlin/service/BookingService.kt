package service

import data.Data
import data.Data.*
import data.PhoneModel
import data.json.JsonWriter
import java.time.Instant

class BookingService(val userName: String) {

    fun release(model: PhoneModel) {
//        val status = Data.PhoneStatus(true, null, null)
        Data.statusData.statusMap.remove(model.modelName)
        JsonWriter.write(Data.statusData)
    }
    fun booked(model: PhoneModel) {
            println(model.modelName)
            val now = Instant.now()
            val status = PhoneStatusData(BookEvent(now, userName))
            Data.statusData.statusMap[model.modelName] = status
            JsonWriter.write(Data.statusData)
        }

}