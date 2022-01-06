package uk.fernando.bluetoothtalk.viewmodel

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
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

        viewModelScope.launch {

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

    fun connectToDevice(device: BluetoothDevice){
        ChatServer.setCurrentChatConnection(device)
    }

//    private fun setupListener() {
//        val filter = IntentFilter()
//        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
//
//        context.registerReceiver(receiver, filter)
//    }

//    private val receiver = object : BroadcastReceiver() {
//
//        override fun onReceive(context: Context, intent: Intent) {
//            when (intent.action) {
//                BluetoothAdapter.ACTION_STATE_CHANGED -> {
//                    System.out.println("******${BluetoothAdapter.STATE_OFF}")
//                    bleManager?.scanState?.tryEmit(BluetoothStatus(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) != BluetoothAdapter.STATE_OFF))
//                }
//            }
//        }
//    }

}