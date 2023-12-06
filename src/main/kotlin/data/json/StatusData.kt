package data.json

import data.Data.*

data class StatusData(val eventMap: MutableMap<String, BookEvent>){
    val statusMap: MutableMap<String, PhoneStatusData> = mutableMapOf( )
    init{
        statusMap.putAll(eventMap.mapValues { PhoneStatusData(it.value) })
    }
}