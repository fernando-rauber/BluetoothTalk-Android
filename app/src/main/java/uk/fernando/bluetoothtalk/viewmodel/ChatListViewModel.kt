package uk.fernando.bluetoothtalk.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uk.fernando.bluetoothtalk.database.entity.UserEntity
import uk.fernando.bluetoothtalk.database.entity.UserWithChat
import uk.fernando.bluetoothtalk.repository.MessageRepository
import javax.inject.Inject


@HiltViewModel
class ChatListViewModel @Inject constructor(repository: MessageRepository) : BaseViewModel() {

    val chatList: MutableState<List<UserWithChat>> = mutableStateOf(listOf())

    init {
        viewModelScope.launch {
//            repository.insertUser(UserEntity("asasas", "Fernando", "https://images.ctfassets.net/hrltx12pl8hq/61DiwECVps74bWazF88Cy9/2cc9411d050b8ca50530cf97b3e51c96/Image_Cover.jpg"))
//            repository.insertUser(UserEntity("48412254", "Asta"))
            repository.getAllUserWithChat().collect {
                chatList.value = it
            }
        }
    }

}