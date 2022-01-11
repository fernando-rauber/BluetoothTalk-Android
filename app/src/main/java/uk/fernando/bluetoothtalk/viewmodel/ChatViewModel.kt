package uk.fernando.bluetoothtalk.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import uk.fernando.bluetoothtalk.database.entity.MessageEntity
import uk.fernando.bluetoothtalk.database.entity.ProfileEntity
import uk.fernando.bluetoothtalk.database.entity.UserEntity
import uk.fernando.bluetoothtalk.ext.TAG
import uk.fernando.bluetoothtalk.repository.MessageRepository
import uk.fernando.bluetoothtalk.service.ble.ChatServer
import uk.fernando.bluetoothtalk.service.model.BleResponse
import uk.fernando.bluetoothtalk.service.model.MessageModel
import uk.fernando.bluetoothtalk.service.model.MessageResponseModel
import uk.fernando.bluetoothtalk.service.model.ResponseType
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: MessageRepository) : BaseViewModel() {

    private var userID: String = ""
    val user: MutableState<UserEntity?> = mutableStateOf(null)
    val messageList: MutableState<List<MessageEntity>> = mutableStateOf(emptyList())
    private lateinit var userProfile: ProfileEntity

    fun fetchMessages(userId: String) {
        if (userID.isNotEmpty())
            return

        userID = userId

        launchDefault {

            userProfile = repository.getProfile()

            user.value = repository.getUserById(userID)

            repository.getMessagesByUser(userID).collect {
                messageList.value = it
            }
        }
        listenNewMessages()
    }

    private fun listenNewMessages() {
        launchDefault {

        }
    }

    fun sendMessage(message: String) {
        launchIO {
            val messageID = repository.insertMessage(MessageEntity(message = message, byMe = true, userId = userID))

            val msg = MessageModel(messageID = messageID, message = message, userID = userProfile.id)
            val bleResponse = BleResponse(type = ResponseType.MESSAGE.value, message = msg)

            ChatServer.sendMessage(bleResponse)
        }
    }


}