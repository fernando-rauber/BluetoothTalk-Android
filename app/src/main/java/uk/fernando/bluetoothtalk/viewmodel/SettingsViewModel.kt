package uk.fernando.bluetoothtalk.viewmodel

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.util.Log
import androidx.core.content.getSystemService
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uk.fernando.bluetoothtalk.BaseApplication
import uk.fernando.bluetoothtalk.ext.TAG
import uk.fernando.bluetoothtalk.repository.MessageRepository
import uk.fernando.bluetoothtalk.repository.UserRepository
import uk.fernando.bluetoothtalk.service.ble.BleScanState.*
import uk.fernando.bluetoothtalk.service.ble.ChatServer
import uk.fernando.bluetoothtalk.service.ble.MyBleManagerScan


class SettingsViewModel (val repository: UserRepository) : BaseViewModel() {

    val isBluetoothOn = MutableStateFlow(false)
    val isScanning = MutableStateFlow(false)
    val devicesNotFound = MutableStateFlow(false)

    init {

    }


}



