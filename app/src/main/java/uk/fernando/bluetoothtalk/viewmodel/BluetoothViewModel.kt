package uk.fernando.bluetoothtalk.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapMerge
import uk.fernando.bluetoothtalk.model.Device
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BluetoothViewModel @Inject constructor() : BaseViewModel() {

//    val binder = MutableStateFlow<SnapItBleService.BleBinder?>(null)

    val isBluetoothOn : MutableState<Boolean> = mutableStateOf(false)
    val isScanning : MutableState<Boolean> = mutableStateOf(false)

    val myDevices: MutableState<List<Device>> = mutableStateOf(listOf())
    val otherDevices: MutableState<List<Device>> = mutableStateOf(listOf())

    fun turnBluetoothOnOff(){

    }

    fun scanForDevices(){
        isScanning.value = !isScanning.value

        val devicesList = mutableListOf<Device>()
        devicesList.add(Device("Fernando", "14:45:b8:65:78", true))
        devicesList.add(Device("Debora", "14:45:b8:65:78"))

        myDevices.value = devicesList



        val otherDevicesList = mutableListOf<Device>()
        otherDevicesList.add(Device("Pedro", "14:45:b8:65:78"))
        otherDevicesList.add(Device("Joao", "14:45:b8:65:78"))
        otherDevicesList.add(Device("Asta", "14:45:b8:65:78"))
        otherDevicesList.add(Device("Kris", "14:45:b8:65:78"))

        otherDevices.value = otherDevicesList
    }
}