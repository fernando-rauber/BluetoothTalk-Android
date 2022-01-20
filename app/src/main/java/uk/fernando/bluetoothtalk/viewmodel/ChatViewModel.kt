package uk.fernando.bluetoothtalk.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import uk.fernando.bluetoothtalk.database.entity.MessageEntity
import uk.fernando.bluetoothtalk.database.entity.ProfileEntity
import uk.fernando.bluetoothtalk.database.entity.UserEntity
import uk.fernando.bluetoothtalk.repository.MessageRepository
import uk.fernando.bluetoothtalk.repository.UserRepository
import uk.fernando.bluetoothtalk.service.ble.BleConnectionState
import uk.fernando.bluetoothtalk.service.ble.ChatServer
import uk.fernando.bluetoothtalk.service.model.BleResponse
import uk.fernando.bluetoothtalk.service.model.MessageModel
import uk.fernando.bluetoothtalk.service.model.ResponseType


class ChatViewModel(private val userRep: UserRepository, private val msgRep: MessageRepository) : BaseViewModel() {

    private var userID: String = ""
    val user: MutableState<UserEntity?> = mutableStateOf(null)
    val messageList: MutableState<List<MessageEntity>> = mutableStateOf(emptyList())
    val isDisconnected = MutableStateFlow(true)
    private lateinit var userProfile: ProfileEntity

    fun fetchMessages(userId: String) {
        if (userID.isNotEmpty())
            return

        userID = userId

        launchDefault {

            userProfile = userRep.getProfile()

            user.value = userRep.getUserById(userID)

            msgRep.getMessagesByUser(userID).collect {
                messageList.value = it
            }
        }
        blueConnectionStatus()
    }

    private fun blueConnectionStatus() {
        launchIO {
            ChatServer.clientConnectionState.collect { state ->
                state?.let {
                    when (state) {
                        is BleConnectionState.Connected -> isDisconnected.value = false
                        is BleConnectionState.Disconnected -> isDisconnected.value = true
                        else -> {}
                    }
                }
            }
        }
    }

    fun sendMessage(message: String) {
        launchIO {
            val messageID = msgRep.insertMessage(MessageEntity(message = message, byMe = true, userId = userID))

            val msg = MessageModel(messageID = messageID, message = message, userID = userProfile.id)
            val bleResponse = BleResponse(type = ResponseType.MESSAGE.value, message = msg)

            ChatServer.sendMessage(bleResponse)
        }
    }

}