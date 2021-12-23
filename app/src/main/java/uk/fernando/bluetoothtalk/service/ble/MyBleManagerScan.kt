package uk.fernando.bluetoothtalk.service.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.*
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import uk.fernando.bluetoothtalk.ext.TAG
import java.util.*

class MyBleManagerScan(private val adapter: BluetoothAdapter) {

    private val scanner: BluetoothLeScanner?
        get() = adapter.bluetoothLeScanner

    val scanState = MutableStateFlow<BleScanState?>(null)

    // String key is the address of the bluetooth device
    private val scanResults = mutableMapOf<String, BluetoothDevice>()

    private var scanCallback: DeviceScanCallback? = null
    private val scanFilters: List<ScanFilter>
    private val scanSettings: ScanSettings

    init {
        // Setup scan filters and settings
        scanFilters = buildScanFilters()
        scanSettings = buildScanSettings()

        getBluetoothStatus()
    }

    private fun getBluetoothStatus() {
        scanState.value = BleScanState.BluetoothStatus(adapter.isEnabled)
    }

    private fun getPairedDevices() {
        val pairedDevices = adapter.bondedDevices?.toList() ?: emptyList<BluetoothDevice>()
        scanState.value = BleScanState.ScanResultsPaired(pairedDevices)
    }

    fun startScan() {
        // If advertisement is not supported on this device then other devices will not be able to
        // discover and connect to it.
        if (adapter.isMultipleAdvertisementSupported == false) {
            Log.e(TAG, "startScan: isMultipleAdvertisementSupported ")
            scanState.value = BleScanState.AdvertisementNotSupported
            return
        }

        if (scanCallback == null) {
            Log.e(TAG, "Start Scanning")
            // Update the UI to indicate an active scan is starting
            scanState.value = BleScanState.ScanStatus(true)
            scanState.value = BleScanState.NotFound(false)

            getPairedDevices()

            // Stop scanning after the scan period
            Handler(Looper.getMainLooper()).postDelayed({
                stopScan()
            }, 15000L)

            // clean & post list
            scanResults.clear()
            scanState.value = BleScanState.ScanResultsOthers(scanResults.values.toList())

            // start new scan
            scanCallback = DeviceScanCallback()
            scanner?.startScan(scanFilters, scanSettings, scanCallback)

        } else {
            Log.e(TAG, "Already scanning")
        }
    }

    private fun stopScan() {
        Log.e(TAG, "Stopping Scanning")
        scanner?.stopScan(scanCallback)
        scanCallback = null

        scanState.value = BleScanState.ScanStatus(false)
        if (scanResults.isEmpty())
            scanState.value = BleScanState.NotFound(true)
    }

    private fun buildScanFilters(): List<ScanFilter> {
        val builder = ScanFilter.Builder()
        // Comment out the below line to see all BLE devices around you
        builder.setServiceUuid(ParcelUuid(SERVICE_UUID))
        val filter = builder.build()
        return listOf(filter)
    }

    private fun buildScanSettings(): ScanSettings {
        return ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()
    }

    private inner class DeviceScanCallback : ScanCallback() {

        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
            results.forEach { item ->
                item.device?.let { device ->
                    scanResults[device.address] = device
                }
            }
            scanState.value = BleScanState.ScanResultsOthers(scanResults.values.toList())
        }

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            result.device?.let { device ->
                scanResults[device.address] = device
            }
            scanState.value = BleScanState.ScanResultsOthers(scanResults.values.toList())
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            val errorMessage = "Scan failed with error: $errorCode"
            scanState.value = BleScanState.Error(errorMessage)
        }
    }

    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("0000b81d-0000-1000-8000-00805f9b34fb")
        val MESSAGE_UUID: UUID = UUID.fromString("7db3e235-3608-41f3-a03c-955fcbd2ea4b")
        val CONFIRM_UUID: UUID = UUID.fromString("36d4dc5c-814b-4097-a5a6-b93b39085928")


        private val TX_CHAR = UUID.fromString("569a2000-b87f-490c-92cb-11ba5ea5167c")
        private val RX_CHAR = UUID.fromString("569a2001-b87f-490c-92cb-11ba5ea5167c")
//        private val BATTERY_UUID = UUID.fromString("0x2A19")

        const val DUMMY_DEVICE_ADDRESS = "F1:79:17:94:03:7B"
    }
}