package app

import data.json.JsonHook
import data.json.model.StatusData
import app.service.BookingService
import ui.BookingMenuManager

class AppManager {
    lateinit var swing: BookingMenuManager
    lateinit var dialog: DialogManager
    lateinit var dataManager: AppDataManager
    lateinit var bookingService: BookingService
    lateinit var hook: JsonHook

    fun createStatusData(): StatusData {
        return dataManager.readAndCreateStatusData()
    }

    fun updateGUI() {
        if (dataManager.updateAndGetDirty()) {
            swing.updateGUI(dataManager.data.statusData)
        }
    }

}