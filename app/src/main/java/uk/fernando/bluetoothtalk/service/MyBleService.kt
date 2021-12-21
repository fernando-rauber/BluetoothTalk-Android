package uk.fernando.bluetoothtalk.service


import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import uk.fernando.bluetoothtalk.model.Device


@AndroidEntryPoint
class MyBleService : LifecycleService() {

    private val binder = BleBinder()
    private var bluetoothService: BluetoothManager? = null

    val otherDevices = MutableStateFlow<List<Device>>(emptyList())
    val isSearching = MutableStateFlow(false)


    val isBluetoothOn: Flow<Boolean> = flow {
        bluetoothService?.adapter?.isEnabled
    }

    fun getPairedDevices(): List<Device> {
        return bluetoothService?.adapter?.bondedDevices?.map {
            Device(it.name, it.address)
        } ?: emptyList()
    }

    fun startSearch() {
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)

        registerReceiver(receiver, filter)

        if (bluetoothService?.adapter?.isDiscovering == true)
            bluetoothService?.adapter?.cancelDiscovery()

        bluetoothService?.adapter?.startDiscovery()
    }

    fun cancelSearch() {
        if (bluetoothService?.adapter?.isDiscovering == true)
            bluetoothService?.adapter?.cancelDiscovery()

        isSearching.tryEmit(false)
    }

    fun disableBle() {
        if (bluetoothService?.adapter?.isEnabled == true)
            bluetoothService?.adapter?.disable()
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {

        val list: HashMap<String, Device> = HashMap()

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name ?: ""
                    val deviceAddress = device?.address // MAC address
                    deviceAddress?.let {
                        list[deviceAddress] = Device(deviceName, deviceAddress)
                        otherDevices.tryEmit(list.values.toList())
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    isSearching.tryEmit(true)
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    isSearching.tryEmit(false)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        bluetoothService = getSystemService<BluetoothManager>()
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId).let { Service.START_STICKY }
    }

    inner class BleBinder : Binder() {

        // Return this instance of LocalService so clients can call public methods
        fun getService(): MyBleService = this@MyBleService
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

}