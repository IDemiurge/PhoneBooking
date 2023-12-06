package app.service

import app.AppHandler
import app.AppManager
import data.json.model.PhoneModel
import data.json.model.StatusData
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BookingService(val userName: String, manager: AppManager) : AppHandler(manager) {

    fun release(model: PhoneModel) {
        manager.dataManager.delete(model)
    }

    fun bookPhone(model: PhoneModel) {
        println(model.modelName)
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy', at 'HH:mm")

        val now = formatter.format(LocalDateTime.now()) ?: ""
        val status = StatusData.PhoneStatusData(StatusData.BookEvent(now, userName))
        manager.dataManager.update(model, status)
    }

}