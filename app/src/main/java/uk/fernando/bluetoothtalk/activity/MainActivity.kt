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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.compose.inject
import uk.fernando.bluetoothtalk.components.BluetoothStatusDialog
import uk.fernando.bluetoothtalk.components.BottomNavigationBar
import uk.fernando.bluetoothtalk.database.entity.MessageEntity
import uk.fernando.bluetoothtalk.database.entity.UserEntity
import uk.fernando.bluetoothtalk.ext.TAG
import uk.fernando.bluetoothtalk.navigation.Directions
import uk.fernando.bluetoothtalk.navigation.buildGraph
import uk.fernando.bluetoothtalk.repository.MessageRepository
import uk.fernando.bluetoothtalk.repository.UserRepository
import uk.fernando.bluetoothtalk.service.ble.BleConnectionState
import uk.fernando.bluetoothtalk.service.ble.ChatServer
import uk.fernando.bluetoothtalk.service.model.BleResponse
import uk.fernando.bluetoothtalk.service.model.MessageModel
import uk.fernando.bluetoothtalk.service.model.ProfileModel
import uk.fernando.bluetoothtalk.service.model.ResponseType
import uk.fernando.bluetoothtalk.theme.MyTheme

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {

//    private val serviceObserver = ServiceBinderLifecycleObserver(this)

    //    init {
//        lifecycle.addObserver(serviceObserver)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ChatServer.startServer(application)


        setContent {

            val userRep: UserRepository by inject()
            val msgRep: MessageRepository by inject()


            val controller = rememberNavController()
            val navBackStackEntry by controller.currentBackStackEntryAsState()
            val statusDialog by remember { mutableStateOf(mutableListOf<String>()) }
            var showDialog by remember { mutableStateOf(0) }

            messagesObserver(userRep, msgRep) {
                statusDialog.clear()
                showDialog = 0

                controller.navigate(it)
            }

            clientObserver(userRep) {
                Log.e(TAG, "onCreate1: $it")
                showDialog++
                statusDialog.add(it)
            }
            serverObserver {
                Log.e(TAG, "onCreate2: $it")
                showDialog++
                statusDialog.add(it)
            }

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

                        if (showDialog > 0) {
                            BluetoothStatusDialog(Modifier.align(Alignment.Center), statusDialog)
                        }
                    }

                }
            }
        }
    }

    private fun messagesObserver(userRep: UserRepository, msgRep: MessageRepository, navigate: (String) -> Unit) {
        lifecycleScope.launch {
            ChatServer.receivedMessage.collect { response ->
                response?.let {
                    when (it.type) {
                        ResponseType.MESSAGE.value -> insertReceivedMessage(msgRep, it.message!!)

                        ResponseType.MESSAGE_RESPONSE.value -> msgRep.updateMessageToSent(it.messageResponse!!.messageID)

                        ResponseType.PROFILE.value -> {
                            Log.e(TAG, "PROFILE received: ${response.profile?.userID}")
                            val profile = response.profile!!
                            userRep.insertUser(UserEntity(profile.userID, profile.name, profile.photo))
                            Log.e(TAG, "PROFILE added")
                            ChatServer.connectToCurrentUser()

                            navigate(Directions.chat.name.plus("/${profile.userID}"))
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


    private fun clientObserver(userRep: UserRepository, onStatusChange: (String) -> Unit) {
        lifecycleScope.launch {

            // Device Connection Observer
            ChatServer.clientConnectionState.collect { state ->
                state?.let {
                    when (state) {
                        is BleConnectionState.Connecting -> onStatusChange("connecting")
                        is BleConnectionState.Connected -> {
                            onStatusChange("connected to ${state.device}")
                            onStatusChange("Requesting device ${state.device}'s connection")

                            Log.e(TAG, "REQUEST_PROFILE: profile")
                            val profile = userRep.getProfile()
                            val profileModel = ProfileModel(userID = profile.id, name = profile.name)
                            val bleResponse = BleResponse(type = ResponseType.PROFILE.value, profile = profileModel)
                            delay(1000)
                            ChatServer.sendMessage(bleResponse)
                            Log.e(TAG, "REQUEST_PROFILE: sent")

                        }
                        is BleConnectionState.GotConnectedBy -> onStatusChange("got connected by: ${state.device}")
                        is BleConnectionState.Disconnected -> onStatusChange("disconnected")
                        is BleConnectionState.ConnectionEstablished -> onStatusChange("Connection Established")
                    }
                }
            }
        }
    }

    private fun serverObserver(onStatusChange: (String) -> Unit) {
        lifecycleScope.launch {

            // Device Connection Observer
            ChatServer.serverConnectionState.collect { state ->
                state?.let {
                    when (state) {
                        is BleConnectionState.Connecting -> onStatusChange("**Server connecting")
                        is BleConnectionState.Connected -> onStatusChange("**Server connected")
                        is BleConnectionState.Disconnected -> onStatusChange("**Server disconnected")
                        else -> {}
                    }
                }
            }
        }
    }

//    private fun initObservers3() {
//        launchDefault {
//            ChatServer.receivedMessage.collect { response ->
//                if (response != null && response.type == ResponseType.PROFILE.value) {
//                    Log.e(TAG, "initObservers3: profile ${response.profile?.userID}")
//                    val profile = response.profile!!
//                    repository.insertUser(UserEntity(profile.userID, profile.name, profile.photo))
//                    navChat.value = profile.userID
//                }
//            }
//        }
//    }

    private suspend fun insertReceivedMessage(msgRep: MessageRepository, message: MessageModel) {
        Log.e(TAG, " New message: \"${message.message}\"")
        msgRep.insertMessage(MessageEntity(message = message.message, userId = message.userID))

//        val messageResponse = MessageResponseModel(messageID = message.messageID, hasReceived = true)
//        val bleResponse = BleResponse(type = ResponseType.MESSAGE_RESPONSE.value, messageResponse = messageResponse)
//
//        // Send response says that received message
//        ChatServer.sendMessage(bleResponse)
    }
}