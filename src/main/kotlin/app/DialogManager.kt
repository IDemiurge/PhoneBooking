package app

import data.json.model.PhoneModel
import data.json.model.StatusData
import ui.BookingMenu
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

class DialogManager(val component: BookingMenu, manager: AppManager): AppHandler(manager) {

    fun prompt(model: PhoneModel) {
        val data: StatusData.PhoneStatusData = manager.dataManager.getStatus(model)
        var message = "Choose an option for ${model.modelName}"

        lateinit var actionName: String
        lateinit var callback: (PhoneModel) -> Unit
        lateinit var options: Array<String>

        data.status.let {
            when (it) {
                StatusData.BookingStatus.FREE -> {
                    actionName = "Book"
                    callback = { book(model) }
                    options = arrayOf(actionName, "Show bands info", "Cancel")
                }

                StatusData.BookingStatus.BOOKED_BY_THIS_USER -> {
                    actionName = "Release"
                    callback = { release(model) }
                    options = arrayOf(actionName, "Show bands info", "Show booking details", "Cancel")
                }

                StatusData.BookingStatus.BOOKED_BY_ANOTHER_USER -> {
                    actionName = "Cancel"
                    message = "${model.modelName} is unavailable because it was already booked"
                    callback = {
                    }
                    options = arrayOf(actionName, "Show bands info", "Show booking details")
                }
            }
        }

        val result = JOptionPane.showOptionDialog(
            component , message, "Booking Menu", JOptionPane.OK_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, options, actionName
        )

        if (result == 0)
            callback.invoke(model)
        if (result == 1)
            manager.dialog.showPhoneInfo(model)
        if (result == 2)
            if (data.status != StatusData.BookingStatus.FREE)
                manager.dialog.showBookingInfo(model)
    }

    private fun release(data: PhoneModel) {
        if (SwingUtilities.isEventDispatchThread()) {
            Thread {
                manager.bookingService.release(data)
            }.start()
        }
    }

    private fun book(data: PhoneModel) {
        if (SwingUtilities.isEventDispatchThread()) {
            Thread {
                manager.bookingService.bookPhone(data)
            }.start()
        }
    }

    private fun showPhoneInfo(model: PhoneModel) {
        confirm("${model.modelName} with bands: \n 2g - ${model.bands2g}\n 3g - ${model.bands3g}\n 4g - ${model.bands4g}")
    }

    private fun showBookingInfo(model: PhoneModel) {
        val status = manager.dataManager.getStatus(model)
        confirm(
            "${model.modelName} is booked by ${status?.linkedUserName} on ${status?.timestamp}")
    }

    private fun confirm(s: String) {
        JOptionPane.showMessageDialog(component, s, "Confirm", JOptionPane.INFORMATION_MESSAGE)
    }
}