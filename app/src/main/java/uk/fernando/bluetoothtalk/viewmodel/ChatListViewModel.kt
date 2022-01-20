package uk.fernando.bluetoothtalk.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.collect
import uk.fernando.bluetoothtalk.database.entity.UserWithMessage
import uk.fernando.bluetoothtalk.repository.UserRepository


class ChatListViewModel(private val repository: UserRepository) : BaseViewModel() {

    val chatList: MutableState<List<UserWithMessage>> = mutableStateOf(listOf())

    init {
        launchIO {
            repository.getAllUserWithMessage().collect {
                chatList.value = it
            }
        }
    }

    fun deleteChat(user: UserWithMessage) {
        launchIO {
            repository.deleteUserAndMessages(user.user)
        }
    }
}