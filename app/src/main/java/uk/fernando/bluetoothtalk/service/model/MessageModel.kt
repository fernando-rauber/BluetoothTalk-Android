package uk.fernando.bluetoothtalk.service.model

enum class ResponseType(val value: Int) {
    MESSAGE(1),
    MESSAGE_RESPONSE(2),
    PROFILE(3)
//    REQUEST_PROFILE(4)
}

data class BleResponse(
    val type: Int,

    val message: MessageModel? = null,
    val messageResponse: MessageResponseModel? = null,
    val profile: ProfileModel? = null,
)

data class MessageModel(
    val messageID: Long,
    val message: String,
    val userID: String
)

data class MessageResponseModel(
    val messageID: Long,
    val hasReceived: Boolean
)

data class ProfileModel(
    val userID: String,
    val name: String,
    val photo: String = ""
)