package uk.fernando.bluetoothtalk.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uk.fernando.bluetoothtalk.components.BottomNavigationBar
import uk.fernando.bluetoothtalk.database.entity.MessageEntity
import uk.fernando.bluetoothtalk.database.entity.UserEntity
import uk.fernando.bluetoothtalk.ext.TAG
import uk.fernando.bluetoothtalk.navigation.Directions
import uk.fernando.bluetoothtalk.navigation.buildGraph
import uk.fernando.bluetoothtalk.repository.MessageRepository
import uk.fernando.bluetoothtalk.service.ble.ChatServer
import uk.fernando.bluetoothtalk.service.model.*
import uk.fernando.bluetoothtalk.theme.MyTheme
import javax.inject.Inject

@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

//    private val serviceObserver = ServiceBinderLifecycleObserver(this)

    //    init {
//        lifecycle.addObserver(serviceObserver)
//    }
    @Inject
    lateinit var repository: MessageRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ChatServer.startServer(application)
        observer()

        setContent {
            val controller = rememberNavController()
            val navBackStackEntry by controller.currentBackStackEntryAsState()

            MyTheme {

                Scaffold(
                    bottomBar = {
                        when (navBackStackEntry?.destination?.route) {
                            Directions.bluetooth.name, Directions.chatList.name, Directions.settings.name ->
                                BottomNavigationBar(controller)
                        }
                    }
                ) { padding ->

                    Box(modifier = Modifier.padding(padding)) {
                        NavHost(
                            navController = controller,
                            startDestination = Directions.chatList.name
                        ) {
                            buildGraph(controller)
                        }
                    }

                }
            }
        }
    }

    private fun observer() {
        lifecycleScope.launch {
            ChatServer.receivedMessage.collect { response ->
                response?.let {
                    when (it.type) {
                        ResponseType.MESSAGE.value -> insertReceivedMessage(it.message!!)

                        ResponseType.MESSAGE_RESPONSE.value -> repository.updateMessageToSent(it.messageResponse!!.messageID)

                        ResponseType.PROFILE.value -> {
                            Log.e(TAG, "PROFILE received: ${response.profile?.userID}")
                            val profile = response.profile!!
                            repository.insertUser(UserEntity(profile.userID, profile.name, profile.photo))
                        }

//                        ResponseType.REQUEST_PROFILE.value -> {
//                            Log.e(TAG, "REQUEST_PROFILE: profile")
//                            val profile = repository.getProfile()
//                            val profileModel = ProfileModel(userID = profile.id, name = profile.name)
//                            val bleResponse = BleResponse(type = ResponseType.PROFILE.value, profile = profileModel)
//                            delay(200)
//                            ChatServer.sendMessage(bleResponse)
//                            Log.e(TAG, "REQUEST_PROFILE: sent")
//
//                            //navChat.value = profile.userID
//                        }
                    }
                }
            }
        }
    }

    private suspend fun insertReceivedMessage(message: MessageModel) {
        Log.e(TAG, " New message: \"${message.message}\"")
        repository.insertMessage(MessageEntity(message = message.message, userId = message.userID))

//        val messageResponse = MessageResponseModel(messageID = message.messageID, hasReceived = true)
//        val bleResponse = BleResponse(type = ResponseType.MESSAGE_RESPONSE.value, messageResponse = messageResponse)
//
//        // Send response says that received message
//        ChatServer.sendMessage(bleResponse)
    }
}