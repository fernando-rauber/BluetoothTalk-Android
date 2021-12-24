package uk.fernando.bluetoothtalk.service


import android.app.Service
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uk.fernando.bluetoothtalk.service.ble.BleScanState
import uk.fernando.bluetoothtalk.service.ble.ChatServer
import uk.fernando.bluetoothtalk.service.ble.MyBleManagerScan
import java.util.*


@AndroidEntryPoint
class MyBleService : LifecycleService() {

    private val binder = BleBinder()
    private var bluetoothService: BluetoothManager? = null

    private lateinit var bleScanner: MyBleManagerScan

    val scanState = MutableStateFlow<BleScanState?>(null)

    private fun initBleScanner() {
        lifecycleScope.launch {
            bleScanner.scanState.collect {
                scanState.value = it
            }
        }
    }

    fun startScan() {
        bleScanner.startScan()
    }

    fun enableDisableBle() {
        if (bluetoothService?.adapter?.isEnabled == false)
            bluetoothService?.adapter?.enable()
        else
            bluetoothService?.adapter?.disable()
    }

    override fun onCreate() {
        super.onCreate()
        bluetoothService = getSystemService()
        bleScanner = MyBleManagerScan(bluetoothService!!.adapter)
        initBleScanner()
        ChatServer.startServer(application)
    }

    override fun onDestroy() {
        super.onDestroy()
        ChatServer.stopServer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId).let { Service.START_STICKY }
    }

    inner class BleBinder : Binder() {

        fun getService(): MyBleService = this@MyBleService
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }
}