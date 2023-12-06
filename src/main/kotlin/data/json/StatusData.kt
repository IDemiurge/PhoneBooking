package data.json

import data.Data.*

data class StatusData(val eventMap: MutableMap<String, BookEvent>){
    val statusMap: MutableMap<String, PhoneStatus> = mutableMapOf( )
    init{
        statusMap.putAll(eventMap.mapValues { PhoneStatus(it.value) })
    }
}