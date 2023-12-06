import data.Data
import data.json.JsonHook
import data.json.JsonReader
import data.json.PhonesData
import data.json.StatusData
import ui.BookingMenu
import javax.swing.JFrame
import kotlinx.coroutines.*
import service.BookingService
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

class Main {
    companion object {
        private var lastReadStatusData: String = ""
        val phoneDataDefer = CompletableDeferred<PhonesData>()
        val phoneStatusDefer = CompletableDeferred<StatusData>()
        lateinit var menu: BookingMenu
        val userName =  JOptionPane.showInputDialog("user name?")
        fun main() {
//        runBlocking {
            Thread {
                readAndHookOnJSON()
            }.start()
//        }
            runBlocking {
                createAndShowGUI()
            }
        }

        private fun readAndHookOnJSON() {
            phoneDataDefer.complete(JsonReader.readPhonesData())
            phoneStatusDefer.complete(JsonReader.readStatusData())
            JsonHook.initHook { s ->
                run {
                    val update = updateAndGetStatusData(s)
                    if (update)
                        SwingUtilities.invokeLater { menu.updateGUI(Data.statusData) }
                }
            }
            //TODO HOOK and start running updates
        }

        private fun updateAndGetStatusData(s: String): Boolean {
            if (!s.equals(lastReadStatusData)) {
                var newData = JsonReader.readStatusData()
                Data.statusData.statusMap.clear()
                Data.statusData.statusMap.putAll(newData.statusMap)
                println("Updated status data with: \n$s")
                lastReadStatusData = s
                return true
            }
            return false
        }

        private suspend fun createAndShowGUI() {
            val frame = JFrame("Booking App").apply {
                defaultCloseOperation = JFrame.EXIT_ON_CLOSE
                setSize(300, 800)
            }
            menu = BookingMenu(BookingService(userName), phoneDataDefer.await())
            frame.add(menu)
            frame.isVisible = true
            Data.statusData = phoneStatusDefer.await()
            menu.updateGUI(Data.statusData)
        }
    }
}