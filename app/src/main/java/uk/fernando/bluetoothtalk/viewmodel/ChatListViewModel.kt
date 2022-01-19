package uk.fernando.bluetoothtalk.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uk.fernando.bluetoothtalk.database.entity.UserWithMessage
import uk.fernando.bluetoothtalk.repository.MessageRepository


class ChatListViewModel (repository: MessageRepository) : BaseViewModel() {

    val chatList: MutableState<List<UserWithMessage>> = mutableStateOf(listOf())

    init {
        viewModelScope.launch {
//            repository.insertUser(UserEntity("asasas", "Fernando", "https://images.ctfassets.net/hrltx12pl8hq/61DiwECVps74bWazF88Cy9/2cc9411d050b8ca50530cf97b3e51c96/Image_Cover.jpg"))
//            repository.insertUser(UserEntity("48412254", "Asta"))
            repository.getAllUserWithMessage().collect {
                chatList.value = it
            }
        }
    }

}