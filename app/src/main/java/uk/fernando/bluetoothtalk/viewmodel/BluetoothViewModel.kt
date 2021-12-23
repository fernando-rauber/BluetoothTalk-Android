package uk.fernando.bluetoothtalk.viewmodel

import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uk.fernando.bluetoothtalk.BaseApplication
import uk.fernando.bluetoothtalk.service.MyBleService
import uk.fernando.bluetoothtalk.service.ble.BleScanState.*
import javax.inject.Inject


@HiltViewModel
class BluetoothViewModel @Inject constructor(val context: BaseApplication) : BaseViewModel() {

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, servic: IBinder) {
            val binder = servic as MyBleService.BleBinder
            bleService.tryEmit(binder.getService())
        }

        override fun onServiceDisconnected(className: ComponentName) {
            bleService.tryEmit(null)
//            context.unbindService(this)
        }
    }

    var bleService = MutableStateFlow<MyBleService?>(null)

    var isBluetoothOn by mutableStateOf(false)
    val isScanning: MutableState<Boolean> = mutableStateOf(false)
    val devicesNotFound = mutableStateOf(false)

    val myDevices: MutableState<List<BluetoothDevice>> = mutableStateOf(listOf())
    val otherDevices: MutableState<List<BluetoothDevice>> = mutableStateOf(listOf())

    init {
        Intent(context, MyBleService::class.java).also { intent ->
            context.bindService(intent, mConnection, Context.BIND_ADJUST_WITH_ACTIVITY)
        }

        viewModelScope.launch {
            bleService.collect { service ->
                service?.let {

                    service.scanState.collect { state ->
                        when (state) {
                            is BluetoothStatus -> isBluetoothOn = state.isOn
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
        }
    }

    fun startScan() {
        bleService.value?.startScan()
    }

    fun enableDisableBle() {
        bleService.value?.enableDisableBle()
    }

}