package uk.fernando.bluetoothtalk.viewmodel

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.util.Log
import androidx.core.content.getSystemService
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uk.fernando.bluetoothtalk.BaseApplication
import uk.fernando.bluetoothtalk.R
import uk.fernando.bluetoothtalk.components.snackbar.SnackBarSealed
import uk.fernando.bluetoothtalk.database.entity.UserEntity
import uk.fernando.bluetoothtalk.ext.TAG
import uk.fernando.bluetoothtalk.repository.MessageRepository
import uk.fernando.bluetoothtalk.service.ble.BleConnectionState.*
import uk.fernando.bluetoothtalk.service.ble.BleScanState.*
import uk.fernando.bluetoothtalk.service.ble.ChatServer
import uk.fernando.bluetoothtalk.service.ble.MyBleManagerScan
import uk.fernando.bluetoothtalk.service.model.BleResponse
import uk.fernando.bluetoothtalk.service.model.ProfileModel
import uk.fernando.bluetoothtalk.service.model.ResponseType
import javax.inject.Inject


@HiltViewModel
class BluetoothViewModel @Inject constructor(val context: BaseApplication, val repository: MessageRepository) : BaseViewModel() {

    private var bluetoothService: BluetoothManager? = null
    private var bleManager: MyBleManagerScan? = null

    val isBluetoothOn = MutableStateFlow(false)
    val isScanning = MutableStateFlow(false)
    val devicesNotFound = MutableStateFlow(false)

    val myDevices = MutableStateFlow<List<BluetoothDevice>>(listOf())
    val otherDevices = MutableStateFlow<List<BluetoothDevice>>(listOf())

    val navChat = MutableStateFlow("")

    private var userID: String = ""
    init {
        bluetoothService = context.getSystemService()
        bleManager = MyBleManagerScan(bluetoothService!!.adapter)

        initObservers()
        initObservers2()
    }

    fun enableDisableBle() {
        if (bluetoothService?.adapter?.isEnabled == false) {
            bluetoothService?.adapter?.enable()
            viewModelScope.launch {
                delay(500)
                bleManager?.getPairedDevices()
            }
        } else
            bluetoothService?.adapter?.disable()
    }

    fun startScan() {
        bleManager?.startScan()
    }

    fun connectToDevice(device: BluetoothDevice) {
        Log.e(TAG, "connectToDevice")
        ChatServer.setCurrentChatConnection(device)
    }

    private fun initObservers() {
        launchDefault {

            // Scan Status Observer
            bleManager?.scanState?.collect { state ->

                when (state) {
                    is BluetoothStatus -> {
                        isBluetoothOn.value = state.isOn
                        if (state.isOn)
                            bleManager?.getPairedDevices()
                    }
                    is ScanStatus -> isScanning.value = state.isOn
                    is ScanResultsPaired -> myDevices.value = state.pairedResults
                    is ScanResultsOthers -> otherDevices.value = state.othersResults
                    is Error -> {}
                    is AdvertisementNotSupported -> {}
                    is NotFound -> devicesNotFound.value = state.notFound
                }
            }
        }
    }

    private fun initObservers2() {
        launchDefault {

            // Device Connection Observer
            ChatServer.deviceConnectionState.collect { state ->
                state?.let {
                    when (state) {
                        is Connecting -> snackBar.value = SnackBarSealed.Success(R.string.connecting, isLongDuration = true)
                        is Connected -> {
//                            val bleResponse = BleResponse(type = ResponseType.REQUEST_PROFILE.value)
//
//                            delay(2000)
//                            ChatServer.sendMessage(bleResponse)
                            ChatServer.setCurrentChatConnection(state.device)


                            snackBar.value = SnackBarSealed.Success(messageText = "Connected to ${state.device.address}")
                            //TODO send user to
                        }
                        is Disconnected -> snackBar.value = SnackBarSealed.Error(R.string.disconnected)
                        is ConnectionFailed -> snackBar.value = SnackBarSealed.Error(R.string.connection_failed)
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

}



