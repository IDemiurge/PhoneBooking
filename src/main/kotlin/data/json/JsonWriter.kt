package data.json

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonToken
import data.Data
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter

class JsonWriter {

    object InstantTypeAdapter : TypeAdapter<Instant>() {
        override fun write(out: com.google.gson.stream.JsonWriter?, value: Instant?) {
            if (value == null) {
                out?.nullValue()
            } else {
                out?.value(DateTimeFormatter.ISO_INSTANT.format(value))
            }
        }

        override fun read(`in`: com.google.gson.stream.JsonReader?): Instant? {
            if (`in`?.peek() == JsonToken.NULL) {
                `in`.nextNull()
                return null
            }
            val instantString = `in`?.nextString()
            return Instant.from(DateTimeFormatter.ISO_INSTANT.parse(instantString))
        }
    }

    companion
    object {
        fun write(data: StatusData){
            val gson: Gson = GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Instant::class.java, InstantTypeAdapter)
                .create()
            val resource = JsonWriter::class.java.classLoader.getResource("json/status_data.json").toURI()
            val file = File(resource)
            val type = object : TypeToken<Map<String, Data.BookEvent>>() {}.type
            val map : MutableMap<String, Data.BookEvent> = mutableMapOf( )
            map.putAll(data.statusMap.mapValues { Data.BookEvent(it.value.timestamp,it.value.linkedUserName) })
            file.writeText( gson.toJson(map, type))
        }
    }
}