package data.json

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.json.model.PhoneModel
import data.json.model.StatusData
import java.io.File

class MyJsonReader(private val gson: Gson) {

    fun readPhonesData(): List<PhoneModel> {
        val listType = object : TypeToken<List<PhoneModel>>() {}.type
        val content = readPhonesDataContent()
        val data: List<PhoneModel> = gson.fromJson(content, listType)
        return (data)
    }

    fun readStatusData(): MutableMap<String, StatusData.BookEvent> {
        val content = readStatusDataContent()
        if (content != null) {
            return parse(content)
        } else {
            createNewStatusFile()
            return mutableMapOf()
        }
    }

    fun readPhonesDataContent(): String {
        val resourceAsStream = MyJsonReader::class.java.classLoader.getResourceAsStream("json/phones_data.json")
        if (resourceAsStream != null) {
            return resourceAsStream.bufferedReader().use { it.readText() }
        } else {
            println("Resource not found")
            throw RuntimeException("fail!")
        }
    }


    fun parse(content: String): MutableMap<String, StatusData.BookEvent> {
        val listType = object : TypeToken<MutableMap<String, StatusData.BookEvent>>() {}.type
        return gson.fromJson(content, listType) ?: mutableMapOf()
    }

    private fun createNewStatusFile() {
        val resource = MyJsonWriter::class.java.classLoader.getResource("json").toString().substringAfter('/')
            .replace("%20", " ")
        val file = File("$resource/status_data.json")
        println("Resource not found, creating $file.getAbsolutePath()")
        file.createNewFile()
    }

    fun readStatusDataContent(): String? {
        val resourceAsStream = MyJsonReader::class.java.classLoader.getResourceAsStream("json/status_data.json")
        if (resourceAsStream != null) {
            return resourceAsStream.bufferedReader().use { it.readText() }
        } else {
            println("Resource not found")
            return null
        }
    }

}