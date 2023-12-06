package app

import com.google.gson.GsonBuilder
import data.json.*
import data.json.model.PhoneModel
import data.json.model.StatusData
import app.service.StatusConverter
import data.AppData
import java.time.Instant
import java.time.LocalDateTime
import javax.swing.JOptionPane

class AppDataManager(statusInit: () -> StatusData) {

    val data: AppData
    val userName = JOptionPane.showInputDialog("Input user name", "Test User") ?: "Test User"

    private var lastReadStatusContent: String? = ""
    private var writer: MyJsonWriter
    private var reader: MyJsonReader

    init {
        val gson = GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime::class.java, MyJsonWriter.LocalDateTimeTypeAdapter)
            .create()
        StatusConverter.userName = userName
        reader = MyJsonReader(gson)
        writer = MyJsonWriter(gson)

        data = AppData(reader.readPhonesData(), statusInit)
    }

    fun readAndCreateStatusData(): StatusData {
        return StatusData(reader.readStatusData())
    }

    fun updateAndGetDirty(): Boolean {
        val content = reader.readStatusDataContent()
        if (!equals(lastReadStatusContent)) {
            val newData = reader.parse(content ?: "")
            data.statusData.statusMap.clear()
            data.statusData.statusMap.putAll(
                StatusConverter.convert(newData)
            )
            println("Updated status data with: \n$content")
            lastReadStatusContent = content
            return true
        }
        return false
    }

    fun getStatus(model: PhoneModel): StatusData.PhoneStatusData {
        return data.statusData.statusMap[model.modelName] ?: StatusData.PhoneStatusData(null)
    }

    fun delete(model: PhoneModel) {
        data.statusData.statusMap.remove(model.modelName)
        persist()
    }

    fun update(model: PhoneModel, status: StatusData.PhoneStatusData) {
        data.statusData.statusMap[model.modelName] = status
        persist()
    }

    private fun persist() {
        writer.write(StatusConverter.convertBack(data.statusData.statusMap))
    }
}
