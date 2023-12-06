package data.json

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import data.json.model.StatusData
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyJsonWriter(private val gson: Gson) {

    object LocalDateTimeTypeAdapter : TypeAdapter<LocalDateTime>() {
        override fun write(out: JsonWriter?, value: LocalDateTime?) {
            if (value == null) {
                out?.nullValue()
            } else {
                out?.value(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(value))
            }
        }

        override fun read(`in`: JsonReader?): LocalDateTime? {
            if (`in`?.peek() == JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            val instantString = `in`?.nextString()
            return LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(instantString))
        }
    }

        fun write(data: MutableMap<String, StatusData.BookEvent>){
            val resource = MyJsonWriter::class.java.classLoader.getResource("json/status_data.json").toURI()
            val file = File(resource)
            val type = object : TypeToken<Map<String, StatusData.BookEvent>>() {}.type
            file.writeText( gson.toJson(data, type))
        }
}