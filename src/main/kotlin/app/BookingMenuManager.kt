package app

import data.json.model.StatusData
import ui.BookingMenu
import java.awt.Color
import javax.swing.JButton
import javax.swing.SwingUtilities

class BookingMenuManager {
    lateinit var component: BookingMenu

    fun updateGUI(data: StatusData) {
        SwingUtilities.invokeLater { component.updateGUI(data) }
    }
    fun getText(modelName: String, status: StatusData.BookingStatus): String {
        return when (status) {
            StatusData.BookingStatus.FREE -> "[Free] $modelName"
            StatusData.BookingStatus.BOOKED_BY_ANOTHER_USER -> "[Unavailable] $modelName"
            StatusData.BookingStatus.BOOKED_BY_THIS_USER -> "[Booked] $modelName"
        }
    }
    fun setEnabled(button: JButton?, status: StatusData.BookingStatus) {
        when (status) {
            StatusData.BookingStatus.FREE ->
                button?.background = Color.white

            StatusData.BookingStatus.BOOKED_BY_THIS_USER ->
                button?.background = Color.green

            StatusData.BookingStatus.BOOKED_BY_ANOTHER_USER ->
                button?.background = Color.red
        }
    }
}