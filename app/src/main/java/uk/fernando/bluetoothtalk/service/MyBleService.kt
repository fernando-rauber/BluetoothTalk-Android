package uk.fernando.bluetoothtalk.service


import android.app.Service
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import uk.fernando.bluetoothtalk.service.ble.BleScanState
import uk.fernando.bluetoothtalk.service.ble.ChatServer
import uk.fernando.bluetoothtalk.service.ble.MyBleManagerScan
import java.util.*


@AndroidEntryPoint
class MyBleService : LifecycleService() {

    private val binder = BleBinder()
    private var bluetoothService: BluetoothManager? = null

    private var bleScanner: MyBleManagerScan? = null

    val scanState = MutableStateFlow<BleScanState?>(null)

    fun startScan() {
        bleScanner?.startScan()
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
//        bleScanner = MyBleManagerScan(bluetoothService!!.adapter, scanState)
//        bleScanner?.getBluetoothStatus()
        ChatServer.startServer(application)
    }

    override fun onDestroy() {
        super.onDestroy()
        bleScanner = null
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