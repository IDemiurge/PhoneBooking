package data.json

import java.io.IOException
import java.nio.file.*

class JsonHook {

    companion object {
        private val filePath: Path = Path.of(JsonHook::class.java.classLoader.getResource("json/status_data.json").toURI())
        private val watchService = FileSystems.getDefault().newWatchService()

        fun initHook(hookCallback: (String) -> Unit) {
            watchFileForChanges()

            // Update the UI when the file changes
            Thread {
                while (true) {
                    val key: WatchKey = watchService.take()
                    for (event in key.pollEvents()) {
                        val kind = event.kind()
                        if (kind === StandardWatchEventKinds.ENTRY_MODIFY) {
                            hookCallback(readTextFile())
                        }
                    }
                    key.reset()
                }
            }.start()
        }

        private fun watchFileForChanges() {
            try {
                val directory = filePath.parent
                directory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)
            } catch (e: IOException) {
                println("Error registering file for watching: ${e.message}")
            }
        }

        private fun readTextFile(): String {
            return try {
                filePath.toFile().readText()
            } catch (e: IOException) {
                "Error reading file: ${e.message}"
            }
        }

    }
}