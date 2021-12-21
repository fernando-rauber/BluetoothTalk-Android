package uk.fernando.bluetoothtalk.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uk.fernando.bluetoothtalk.BaseApplication
import uk.fernando.bluetoothtalk.ext.TAG
import uk.fernando.bluetoothtalk.model.Device
import uk.fernando.bluetoothtalk.service.MyBleService
import uk.fernando.bluetoothtalk.service.ServiceBinderLifecycleObserver
import javax.inject.Inject


@HiltViewModel
class BluetoothViewModel @Inject constructor(val context: BaseApplication) : BaseViewModel() {

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, servic: IBinder) {
            val binder = servic as MyBleService.BleBinder
            service.tryEmit(binder.getService())
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.e(TAG, "onServiceDisconnected")
        }
    }

    var service = MutableStateFlow<MyBleService?>(null)

    var isBluetoothOn by mutableStateOf(false)
    val isScanning: MutableState<Boolean> = mutableStateOf(false)

    val myDevices: MutableState<List<Device>> = mutableStateOf(listOf())
    val otherDevices: MutableState<List<Device>> = mutableStateOf(listOf())

    init {
        Intent(context, MyBleService::class.java).also { intent ->
            context.bindService(intent, mConnection, Context.BIND_ADJUST_WITH_ACTIVITY)
        }

        viewModelScope.launch {
            service.collect {
                it?.let {

                    it.isBluetoothOn.collect { isOn ->
                        isBluetoothOn = isOn
                    }

                    it.isSearching.collect{ result ->
                        isScanning.value = result
                    }
                }
            }
        }
    }

    fun startScan() {
        viewModelScope.launch {
            service.value?.let { myService ->
                myService.startSearch()

                myDevices.value = myService.getPairedDevices()

                myService.otherDevices.collect {
                    otherDevices.value = it
                }
            }
        }
    }

    fun cancelScan() {
        service.value?.cancelSearch()
    }
    fun disableBle() {
        service.value?.disableBle()
    }

}