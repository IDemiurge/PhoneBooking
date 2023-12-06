package ui

import data.Data.*
import data.Data.BookingStatus.*
import data.PhoneModel
import data.json.PhonesData
import data.json.StatusData
import service.BookingService
import java.awt.Color
import java.awt.event.ActionEvent
import java.util.function.Consumer
import javax.swing.*


class BookingMenu(val service: BookingService, data: PhonesData) : JPanel() {
    private val buttons = mutableMapOf<String, JButton>()

    init {
        this.layout = BoxLayout(this, BoxLayout.Y_AXIS)
        createButtonsForPhones(data)
    }

    fun createButtonsForPhones(data: PhonesData) {
        data.models.forEach {
            addPhoneButton(it)
        }
    }

    fun addPhoneButton(model: PhoneModel) {
//        callback = ()->
        val text: String = getText(model)
        val callback: Consumer<PhoneModel> = Consumer<PhoneModel> { data -> interact(data) }
        val button = JButton(text)
        button.addActionListener { e: ActionEvent ->
            callback.accept(model)
        }
        add(button)
        buttons.put(model.modelName, button)
    }

    private fun interact(data: PhoneModel) {
        prompt(data, Companion.statusData.statusMap[data.modelName] ?: PhoneStatusData(null))
    }


    private fun prompt(model: PhoneModel, data: PhoneStatusData) {
        var message: String = "Choose an option for " + model.modelName

        lateinit var actionName: String
        lateinit var callback: (PhoneModel) -> Unit
        lateinit var options: Array<String>

        data.status.let {
            when (it) {
                FREE -> {
                    actionName = "Book"
                    callback = { book(model) }
                    options = arrayOf(actionName, "Show bands info", "Cancel")
                }

                BOOKED_BY_THIS_USER -> {
                    actionName = "Release"
                    callback = { release(model) }
                    options = arrayOf(actionName, "Show bands info", "Show booking details", "Cancel")
                }

                BOOKED_BY_ANOTHER_USER -> {
                    actionName = "Cancel"
                    callback = {
                        promptUnavailable(model)
                    }
                    options = arrayOf(actionName, "Show bands info", "Show booking details")
                }
            }
        }
//        options[0] = actionName

        val result = JOptionPane.showOptionDialog(
            this , message, "Booking Menu", JOptionPane.OK_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, options, actionName
        )

        if (result == 0)
            callback.invoke(model)
        if (result == 1)
            showPhoneInfo(model)
        if (result == 2)
            if (data.status != FREE)
                showBookingInfo(model)
    }

    private fun showPhoneInfo(model: PhoneModel) {
        JOptionPane.showConfirmDialog(this, "TODO - bands info ")
    }

    private fun showBookingInfo(model: PhoneModel) {
        JOptionPane.showConfirmDialog(this, "TODO - book info !")
    }

    private fun promptUnavailable(data: PhoneModel) {
        val status = Companion.statusData.statusMap[data.modelName]
        JOptionPane.showConfirmDialog(
            this,
            "${data.modelName} is already booked by ${status?.linkedUserName} at ${status?.timestamp}"
        )
    }

    private fun release(data: PhoneModel) {
        if (SwingUtilities.isEventDispatchThread()) {
            Thread {
                service.release(data)
            }.start()
        }
    }

    private fun book(data: PhoneModel) {
        if (SwingUtilities.isEventDispatchThread()) {
            Thread {
                service.booked(data)
            }.start()
        }
    }

    private fun getText(model: PhoneModel): String {
        return getText(model.modelName, FREE)
    }

    private fun getText(modelName: String, status: BookingStatus): String {
        return when (status) {
            FREE -> "[Free] $modelName"
            BOOKED_BY_ANOTHER_USER -> "[Unavailable] $modelName"
            BOOKED_BY_THIS_USER -> "[Booked] $modelName"
        }
    }


    fun updateGUI(statuses: StatusData) {
        buttons.forEach {
            setEnabled(it.value, FREE) //if status for this phone button is not present in data, it is considered FREE
            val phoneStatus = statuses.statusMap[it.key]
            if (phoneStatus != null) phoneStatus.apply {
                update(buttons[it.key], it.key, this)
            } else {
                update(buttons[it.key], it.key, PhoneStatusData(BookEvent(null, null)))
            }

        }
    }

    private fun update(button: JButton?, modelName: String, value: PhoneStatusData) {
        setEnabled(button, value.status)
        button?.text = getText(modelName, value.status ?: FREE)

        button?.toolTipText = null
        if (value.status != FREE)
            button?.toolTipText = "Booked by $value.linkedUserName at ${value.timestamp.toString()}"
        //tooltip - booked by N at X
    }

    private fun setEnabled(button: JButton?, status: BookingStatus) {
        when (status) {
            FREE ->
                button?.background = Color.white

            BOOKED_BY_THIS_USER ->
                button?.background = Color.green

            BOOKED_BY_ANOTHER_USER ->
                button?.background = Color.red
        }
    }


}