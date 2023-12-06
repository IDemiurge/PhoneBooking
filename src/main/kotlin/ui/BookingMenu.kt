package ui

import app.AppManager
import data.json.model.StatusData.BookingStatus.*
import data.json.model.PhoneModel
import data.json.model.StatusData
import java.util.function.Consumer
import javax.swing.*


class BookingMenu(private val manager: AppManager) : JPanel() {
    private val buttons = mutableMapOf<String, JButton>()

    init {
        this.layout = BoxLayout(this, BoxLayout.Y_AXIS)
        createButtonsForPhones(manager.dataManager.data.phones)
    }

    private fun createButtonsForPhones(data: List<PhoneModel>) {
        data.forEach {
            addPhoneButton(it)
        }
    }

    private fun addPhoneButton(model: PhoneModel) {
        val text: String = getText(model)
        val callback: Consumer<PhoneModel> = Consumer<PhoneModel> { data ->  manager.dialog.prompt(data) }
        val button = JButton(text)
        button.addActionListener {
            callback.accept(model)
        }
        add(button)
        buttons[model.modelName] = button
    }

    private fun getText(model: PhoneModel): String {
        return manager.swing.getText(model.modelName, FREE)
    }

    fun updateGUI(statuses: StatusData) {
        buttons.forEach {
            manager.swing.setEnabled(it.value, FREE) //if status for this phone button is not present in data, it is considered FREE
            val phoneStatus = statuses.statusMap[it.key]
            if (phoneStatus != null) phoneStatus.apply {
                update(buttons[it.key], it.key, this.status)
            } else {
                update(buttons[it.key], it.key, FREE)
            }

        }
    }

    private fun update(button: JButton?, modelName: String, status: StatusData.BookingStatus) {
        manager.swing.setEnabled(button, status)
        button?.text = manager.swing.getText(modelName, status)
    }



}

