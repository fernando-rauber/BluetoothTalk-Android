package uk.fernando.bluetoothtalk.service.ble

import android.bluetooth.BluetoothDevice

sealed class BleConnectionState {
    object Connecting : BleConnectionState()
    class Connected(val device: BluetoothDevice) : BleConnectionState()
    object Disconnected : BleConnectionState()
}