package uk.fernando.bluetoothtalk.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.compose.inject
import uk.fernando.bluetoothtalk.components.BluetoothStatusDialog
import uk.fernando.bluetoothtalk.components.BottomNavigationBar
import uk.fernando.bluetoothtalk.database.entity.MessageEntity
import uk.fernando.bluetoothtalk.database.entity.UserEntity
import uk.fernando.bluetoothtalk.ext.TAG
import uk.fernando.bluetoothtalk.ext.checkBluetoothPermission
import uk.fernando.bluetoothtalk.ext.checkLocationPermission
import uk.fernando.bluetoothtalk.ext.noRippleClickable
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

@ExperimentalAnimationApi
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {

    var profileId = ""

//    private val serviceObserver = ServiceBinderLifecycleObserver(this)

    //    init {
//        lifecycle.addObserver(serviceObserver)
//    }

    @ExperimentalCoilApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {

            val userRep: UserRepository by inject()
            val msgRep: MessageRepository by inject()


            val controller = rememberNavController()
            val navBackStackEntry by controller.currentBackStackEntryAsState()
            val statusDialog by remember { mutableStateOf(mutableListOf<BleConnectionState>()) }
            var showDialog by remember { mutableStateOf(0) }
            val context = LocalContext.current

            // Start Discovery
            context.checkBluetoothPermission(onGranted = { ChatServer.startServer(application) }, {})

            messagesObserver(
                userRep = userRep,
                msgRep = msgRep,
                navigate = {
                    statusDialog.clear()
                    showDialog = 0

                    controller.navigate(it)
                }
            )

            clientObserver(
                userRep = userRep,
                navigate = {
                    statusDialog.clear()
                    showDialog = 0

                    controller.navigate(it)
                },
                onStatusChange = {
                    showDialog++
                    statusDialog.add(it)
                }
            )

//            statusDialog.add(BleConnectionState.Connecting)
//            statusDialog.add(BleConnectionState.Connecting)
//            statusDialog.add(BleConnectionState.Disconnected)
//
//            showDialog++

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
                            ConnectionStatusDialog(statusDialog) {
                                statusDialog.clear()
                                showDialog = 0
                            }
                        }
                    }
                }
            }
        }
    }

    private fun messagesObserver(userRep: UserRepository, msgRep: MessageRepository, navigate: (String) -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            ChatServer.receivedMessage.collect { response ->
                response?.let {
                    when (it.type) {
                        ResponseType.MESSAGE.value -> insertReceivedMessage(msgRep, it.message!!)

                        ResponseType.MESSAGE_RESPONSE.value -> msgRep.updateMessageToSent(it.messageResponse!!.messageID)

                        ResponseType.PROFILE.value -> {
                            val profile = response.profile!!
                            userRep.insertUser(UserEntity(profile.userID, profile.name, profile.photo))
                            ChatServer.connectToCurrentUser()

                            profileId = profile.userID

                            if (ChatServer.clientDevice != null)
                                navigate(Directions.chat.name.plus("/${profile.userID}"))
                        }
                    }
                }
            }
        }
    }

    private fun clientObserver(userRep: UserRepository, navigate: (String) -> Unit, onStatusChange: (BleConnectionState) -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            // Device Connection Observer
            ChatServer.clientConnectionState.collect { state ->
                state?.let {
                    onStatusChange(it)

                    if (state is BleConnectionState.Connected) {
                        val profile = userRep.getProfile()
                        val profileModel = ProfileModel(userID = profile.id, name = profile.name)
                        val bleResponse = BleResponse(type = ResponseType.PROFILE.value, profile = profileModel)
                        delay(1500)
                        ChatServer.sendMessage(bleResponse)

                        if (ChatServer.serverDevice != null)
                            navigate(Directions.chat.name.plus("/${profileId}"))
                    }
                }
            }
        }
    }

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

@Composable
private fun ConnectionStatusDialog(statusDialog: List<BleConnectionState>, onDisconnect: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable { }
            .background(Color.Black.copy(alpha = 0.6f))
    ) {
        BluetoothStatusDialog(Modifier.align(Alignment.Center), statusDialog, onDisconnect)
    }
}