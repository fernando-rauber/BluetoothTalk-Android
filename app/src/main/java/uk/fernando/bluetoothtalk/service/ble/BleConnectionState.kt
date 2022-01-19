package uk.fernando.bluetoothtalk.service.ble

import android.bluetooth.BluetoothDevice

sealed class BleConnectionState {
    // Client
    object Connecting : BleConnectionState()
    class Connected(val device: BluetoothDevice) : BleConnectionState()
    class GotConnectedBy(val device: BluetoothDevice) : BleConnectionState()
    class ConnectionEstablished(val device: BluetoothDevice) : BleConnectionState()
    object Disconnected : BleConnectionState()
}