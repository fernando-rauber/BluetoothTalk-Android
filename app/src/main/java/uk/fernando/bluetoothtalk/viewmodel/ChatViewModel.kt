package uk.fernando.bluetoothtalk.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uk.fernando.bluetoothtalk.database.entity.MessageEntity
import uk.fernando.bluetoothtalk.database.entity.UserEntity
import uk.fernando.bluetoothtalk.ext.TAG
import uk.fernando.bluetoothtalk.repository.MessageRepository
import uk.fernando.bluetoothtalk.service.ble.ChatServer
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: MessageRepository) : BaseViewModel() {

    private var userID: String = ""
    val user: MutableState<UserEntity?> = mutableStateOf(null)
    val messageList: MutableState<List<MessageEntity>> = mutableStateOf(emptyList())

    fun fetchMessages(userId: String) {
        if (userID.isNotEmpty())
            return
        userID = userId

        viewModelScope.launch {

            user.value = repository.getUserById(userID)

//            repository.insertMessage()User(UserEntity("asasas", "Fernando", "https://images.ctfassets.net/hrltx12pl8hq/61DiwECVps74bWazF88Cy9/2cc9411d050b8ca50530cf97b3e51c96/Image_Cover.jpg"))
//            repository.insertUser(UserEntity("48412254", "Asta"))
            repository.getMessagesByUser(userID).collect {
                messageList.value = it
            }
        }
        listenNewMessages()
    }

    private fun listenNewMessages() {
        viewModelScope.launch {
            ChatServer.receivedMessage.collect { newMessage ->
                if (newMessage.isNotEmpty()) {
                    Log.d(TAG, " Have message: \"$newMessage\"")
                    repository.insertMessage(MessageEntity(message = newMessage, userAddress = userID))
                }
            }
        }
    }

    fun sendMessage(message: String) {
        ChatServer.sendMessage(message)
        viewModelScope.launch {
            repository.insertMessage(MessageEntity(message = message, byMe = true, userAddress = userID))
//            repository.insertMessage(MessageEntity(message = "oi", userAddress = address, byMe = true))
//            repository.insertMessage(MessageEntity(message = "ola", userAddress = address, byMe = false))
//            repository.insertMessage(MessageEntity(message = "tudo bom?", userAddress = address, byMe = true))
//            repository.insertMessage(MessageEntity(message = "?", userAddress = address, byMe = true))
        }
    }

}