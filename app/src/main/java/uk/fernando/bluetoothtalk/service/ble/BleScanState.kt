package uk.fernando.bluetoothtalk.service.ble

import android.bluetooth.BluetoothDevice

sealed class BleScanState {
    class BluetoothStatus(val isOn: Boolean) : BleScanState()
    class ScanStatus(val isOn: Boolean) : BleScanState()
    class ScanResultsPaired(val pairedResults: List<BluetoothDevice>) : BleScanState()
    class ScanResultsOthers(val othersResults: List< BluetoothDevice>) : BleScanState()
    class Error(val message: String) : BleScanState()
    object AdvertisementNotSupported : BleScanState()
    class NotFound(val notFound: Boolean) : BleScanState()
}