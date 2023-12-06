package data

data class PhoneModel(
    val modelName: String,
    val bands2g: List<String>,
    val bands3g: List<String>,
    val bands4g: List<String>
)