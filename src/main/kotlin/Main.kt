import app.AppManager
import app.AppDataManager
import data.json.JsonHook
import data.json.model.StatusData
import ui.BookingMenu
import javax.swing.JFrame
import kotlinx.coroutines.*
import app.service.BookingService
import app.DialogManager
import ui.BookingMenuManager

object Main {

    private val updateDefer = CompletableDeferred<StatusData>()
    private lateinit var status: StatusData
    private val manager = AppManager()

    fun main() {
        Thread {
            initAppManager()
        }.start()
        runBlocking {
            createAndShowGUI()
        }
    }

    private fun initAppManager() {
        manager.apply {
            dataManager = AppDataManager { status }
            bookingService = BookingService(dataManager.userName, this)
            swing = BookingMenuManager()
            swing.component = BookingMenu(this)
            dialog = DialogManager(swing.component, this)
            status = createStatusData()
            swing.updateGUI(status)
            updateDefer.complete(status)
            hook = JsonHook {
                updateGUI()
            }
        }
    }


    private suspend fun createAndShowGUI() {
        val frame = JFrame("Phone Booking App").apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            setSize(300, 500)
        }

        updateDefer.await()
        frame.add(manager.swing.component)
        frame.isVisible = true
    }
}