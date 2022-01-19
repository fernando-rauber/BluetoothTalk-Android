package uk.fernando.bluetoothtalk.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uk.fernando.bluetoothtalk.R
import uk.fernando.bluetoothtalk.components.BluetoothStatusDialog
import uk.fernando.bluetoothtalk.components.BottomNavigationBar
import uk.fernando.bluetoothtalk.components.snackbar.SnackBarSealed
import uk.fernando.bluetoothtalk.database.entity.MessageEntity
import uk.fernando.bluetoothtalk.database.entity.UserEntity
import uk.fernando.bluetoothtalk.ext.TAG
import uk.fernando.bluetoothtalk.navigation.Directions
import uk.fernando.bluetoothtalk.navigation.buildGraph
import uk.fernando.bluetoothtalk.repository.MessageRepository
import uk.fernando.bluetoothtalk.screen.GpsDialog
import uk.fernando.bluetoothtalk.service.ble.BleConnectionState
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

    private var isRequestingConnection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ChatServer.startServer(application)


        setContent {
            val controller = rememberNavController()
            val navBackStackEntry by controller.currentBackStackEntryAsState()
            val statusDialog by remember { mutableStateOf(mutableListOf<String>()) }
            var showDialog by remember { mutableStateOf(0) }

            messagesObserver{
                statusDialog.clear()
                showDialog = 0

                controller.navigate(it)
            }

            clientObserver {
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

    private fun messagesObserver(navigate: (String) -> Unit) {
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


    private fun clientObserver(onStatusChange: (String) -> Unit) {
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
                            val profile = repository.getProfile()
                            val profileModel = ProfileModel(userID = profile.id, name = profile.name)
                            val bleResponse = BleResponse(type = ResponseType.PROFILE.value, profile = profileModel)
                            delay(1000)
                            ChatServer.sendMessage(bleResponse)
                            Log.e(TAG, "REQUEST_PROFILE: sent")

                        }
                        is BleConnectionState.GotConnectedBy ->  onStatusChange("got connected by: ${state.device}")
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