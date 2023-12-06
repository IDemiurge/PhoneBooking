package ui

import data.Data.*
import data.Data.BookingStatus.*
import data.json.PhonesData
import data.json.StatusData
import service.BookingService
import java.awt.Color
import java.awt.FlowLayout
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
//        buttons[model] = button
        buttons.put(model.modelName, button)
    }

    private fun interact(data: PhoneModel) {
        var status: BookingStatus = Companion.statusData.statusMap[data.modelName]?.status ?: FREE
        when (status) {
            FREE -> book(data)
            BOOKED_BY_ANOTHER_USER -> promptUnavailable(data)
            BOOKED_BY_THIS_USER -> release(data)
        }
    }

    private fun promptUnavailable(data: PhoneModel) {
        val status = Companion.statusData.statusMap[data.modelName]
        JOptionPane.showConfirmDialog(this, "${data.modelName} is already booked by ${status?.linkedUserName} at ${status?.timestamp}")
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
            FREE -> "Book [$modelName]"
            BOOKED_BY_ANOTHER_USER -> "Unavailable: [$modelName]"
            BOOKED_BY_THIS_USER -> "Release [$modelName]"
        }
    }


    fun updateGUI(statuses: StatusData) {
        buttons.forEach {
            setEnabled(it.value, FREE) //if status for this phone button is not present in data, it is considered FREE
            val phoneStatus = statuses.statusMap[it.key]
            if (phoneStatus != null) phoneStatus.apply {
                update(buttons[it.key], it.key, this)
            } else {
                update(buttons[it.key], it.key, PhoneStatus(BookEvent(null, null)))
            }

        }
    }

    private fun update(button: JButton?, modelName: String, value: PhoneStatus) {
        setEnabled(button, value.status)
        button?.text = getText(modelName, value.status ?: FREE)

        button?.toolTipText = null
        if (value.status != FREE)
            button?.toolTipText = "Booked by $value.linkedUserName at ${value.timestamp.toString()}"
        //tooltip - booked by N at X
    }

    private fun setEnabled(button: JButton?, status: BookingStatus) {
        when (status){
            FREE ->
                button?.background = Color.white
            BOOKED_BY_THIS_USER ->
                button?.background = Color.green
            BOOKED_BY_ANOTHER_USER ->
                button?.background = Color.red
        }
    }


}