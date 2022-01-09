package uk.fernando.bluetoothtalk.viewmodel

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.getSystemService
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uk.fernando.bluetoothtalk.BaseApplication
import uk.fernando.bluetoothtalk.R
import uk.fernando.bluetoothtalk.components.snackbar.SnackBarSealed
import uk.fernando.bluetoothtalk.ext.TAG
import uk.fernando.bluetoothtalk.service.ble.BleConnectionState.*
import uk.fernando.bluetoothtalk.service.ble.BleScanState.*
import uk.fernando.bluetoothtalk.service.ble.ChatServer
import uk.fernando.bluetoothtalk.service.ble.MyBleManagerScan
import javax.inject.Inject


@HiltViewModel
class BluetoothViewModel @Inject constructor(val context: BaseApplication) : BaseViewModel() {

    private var bluetoothService: BluetoothManager? = null

    private var bleManager: MyBleManagerScan? = null

    var isBluetoothOn by mutableStateOf(false)
    val isScanning: MutableState<Boolean> = mutableStateOf(false)
    val devicesNotFound = mutableStateOf(false)

    val myDevices: MutableState<List<BluetoothDevice>> = mutableStateOf(listOf())
    val otherDevices: MutableState<List<BluetoothDevice>> = mutableStateOf(listOf())

    init {
        bluetoothService = context.getSystemService<BluetoothManager>()
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
        viewModelScope.launch {


            // Scan Status Observer
            bleManager?.scanState?.collect { state ->

                when (state) {
                    is BluetoothStatus -> {
                        isBluetoothOn = state.isOn
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
        viewModelScope.launch {

            // Device Connection Observer
            ChatServer.deviceConnectionState.collect { state ->
                state?.let {
                    when (state) {
                        is Connecting -> snackBar.value = SnackBarSealed.Success(R.string.connecting, isLongDuration = true)
                        is Connected -> snackBar.value = SnackBarSealed.Success(messageText = "Connected to ${state.device.name}")
                        is Disconnected -> snackBar.value = SnackBarSealed.Error(R.string.disconnected)
                        is ConnectionFailed -> snackBar.value = SnackBarSealed.Error(R.string.connection_failed)
                    }
                }
            }
        }

    }
}

