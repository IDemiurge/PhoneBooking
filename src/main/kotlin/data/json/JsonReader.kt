package data.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import data.Data
import java.io.File
import java.time.Instant

class  JsonReader {
    //TODO read status data too!
    companion object {
            val gson: Gson = GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Instant::class.java, JsonWriter.InstantTypeAdapter)
                .create()
        fun readPhonesData(): PhonesData{
            val resourceAsStream = JsonReader::class.java.classLoader.getResourceAsStream("json/phones_data.json")
            if (resourceAsStream != null) {
                val content: String = resourceAsStream.bufferedReader().use { it.readText() }
                val listType = object : TypeToken<List<Data.PhoneModel>>() {}.type
                val data: List<Data.PhoneModel> = gson.fromJson(content, listType)
                return PhonesData(data)
            } else {
                println("Resource not found")
                throw RuntimeException("fail!")
            }
        }
        fun readStatusData(): StatusData{
            val resourceAsStream = JsonReader::class.java.classLoader.getResourceAsStream("json/status_data.json")
            if (resourceAsStream != null) {
                val content: String = resourceAsStream.bufferedReader().use { it.readText() }
                val listType = object : TypeToken<MutableMap<String, Data.BookEvent>>() {}.type
                val data: MutableMap<String, Data.BookEvent> = gson.fromJson(content, listType) ?: mutableMapOf()
                return StatusData(data)
            } else {

                val resource = JsonWriter::class.java.classLoader.getResource("json").toString().substringAfter('/')
                    .replace("%20", " ")
                val file = File(resource+"/status_data.json")
                println("Resource not found, creating $file.getAbsolutePath()")
                file.createNewFile()
                return StatusData(mutableMapOf())
            }
        }
    }

}