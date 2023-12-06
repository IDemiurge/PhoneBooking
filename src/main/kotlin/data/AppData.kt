package data

import data.json.model.StatusData
import data.json.model.PhoneModel

data class AppData (val phones: List<PhoneModel>,
                    val statusInit: () -> StatusData){
                    val statusData: StatusData by lazy {
                        statusInit.invoke()
                    }
}