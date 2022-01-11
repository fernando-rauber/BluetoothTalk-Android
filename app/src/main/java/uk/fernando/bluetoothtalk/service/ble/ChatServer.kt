package uk.fernando.bluetoothtalk.service.ble

import android.app.Application
import android.bluetooth.*
import android.bluetooth.BluetoothGatt.CONNECTION_PRIORITY_HIGH
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import uk.fernando.bluetoothtalk.ext.TAG
import uk.fernando.bluetoothtalk.service.ble.MyBleManagerScan.Companion.CONFIRM_UUID
import uk.fernando.bluetoothtalk.service.ble.MyBleManagerScan.Companion.MESSAGE_UUID
import uk.fernando.bluetoothtalk.service.ble.MyBleManagerScan.Companion.SERVICE_UUID
import uk.fernando.bluetoothtalk.service.model.BleResponse
import uk.fernando.bluetoothtalk.service.model.MessageResponseModel
import uk.fernando.bluetoothtalk.service.model.ProfileModel
import uk.fernando.bluetoothtalk.service.model.ResponseType

object ChatServer {

    // hold reference to app context to run the chat server
    private var app: Application? = null
    private lateinit var bluetoothManager: BluetoothManager

    // BluetoothAdapter should never be null if the app is installed from the Play store
    // since BLE is required per the <uses-feature> tag in the AndroidManifest.xml.
    // If the app is installed on an emulator without bluetooth then the app will crash
    // on launch since installing via Android Studio bypasses the <uses-feature> flags
    private val adapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    // This property will be null if bluetooth is not enabled or if advertising is not
    // possible on the device
    private var advertiser: BluetoothLeAdvertiser? = null
    private var advertiseCallback: AdvertiseCallback? = null
    private var advertiseSettings: AdvertiseSettings = buildAdvertiseSettings()
    private var advertiseData: AdvertiseData = buildAdvertiseData()

    // LiveData for reporting connection requests
    private val _connectionRequest = MutableLiveData<BluetoothDevice>()
    val connectionRequest = _connectionRequest as LiveData<BluetoothDevice>

    // for reporting the messages sent to the device
    val receivedMessage = MutableStateFlow<BleResponse?>(null)

    private var gattServer: BluetoothGattServer? = null
    private var gattServerCallback: BluetoothGattServerCallback? = null

    private var gattClient: BluetoothGatt? = null
    private var gattClientCallback: BluetoothGattCallback? = null

    // Properties for current chat device connection
    private var currentDevice: BluetoothDevice? = null

    private var gatt: BluetoothGatt? = null
    private var messageCharacteristic: BluetoothGattCharacteristic? = null

    // fires off every time value of the property changes
    val deviceConnectionState = MutableStateFlow<BleConnectionState?>(null)


    fun startServer(app: Application) {
        bluetoothManager = app.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        if (adapter.isEnabled) {
            setupGattServer(app)
            startAdvertisement()
        }
    }

    fun stopServer() {
        stopAdvertising()
    }

    private fun setupGattServer(app: Application) {
        gattServerCallback = GattServerCallback()

        gattServer = bluetoothManager.openGattServer(app, gattServerCallback).apply {
            addService(setupGattService())
        }
    }

    fun setCurrentChatConnection(device: BluetoothDevice) {
        currentDevice = device

        deviceConnectionState.tryEmit(BleConnectionState.Connecting)
        connectToChatDevice(device)
    }

    private fun connectToChatDevice(device: BluetoothDevice) {
        gattClientCallback = GattClientCallback()
        gattClient = device.connectGatt(app, false, gattClientCallback)
    }

    fun sendMessage(message: BleResponse): Boolean {
        Log.e(TAG, "Send a message")
        messageCharacteristic?.let { characteristic ->
            characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            val messageJson = Gson().toJson(message)
            Log.e(TAG, "Gson***: $messageJson")
            val messageBytes = messageJson.toByteArray(Charsets.UTF_8)
            characteristic.value = messageBytes
            gatt?.let {
                val success = it.writeCharacteristic(messageCharacteristic)
                Log.e(TAG, "onServicesDiscovered: message send: $success")
                if (success) {
                    //_messages.value = Message.LocalMessage(message)
                }
            } ?: run {
                Log.d(TAG, "sendMessage: no gatt connection to send a message with")
            }
        }
        return false
    }

    private fun setupGattService(): BluetoothGattService {
        // Setup gatt service
        val service = BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
        // need to ensure that the property is writable and has the write permission
        val messageCharacteristic = BluetoothGattCharacteristic(
            MESSAGE_UUID,
            BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        service.addCharacteristic(messageCharacteristic)
        val confirmCharacteristic = BluetoothGattCharacteristic(
            CONFIRM_UUID,
            BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        service.addCharacteristic(confirmCharacteristic)

        return service
    }

    private fun startAdvertisement() {
        advertiser = adapter.bluetoothLeAdvertiser
        Log.d(TAG, "startAdvertisement: with advertiser $advertiser")

        if (advertiseCallback == null) {
            advertiseCallback = DeviceAdvertiseCallback()

            advertiser?.startAdvertising(advertiseSettings, advertiseData, advertiseCallback)
        }
    }

    private fun stopAdvertising() {
        Log.d(TAG, "Stopping Advertising with advertiser $advertiser")
        advertiser?.stopAdvertising(advertiseCallback)
        advertiseCallback = null
    }

    private fun buildAdvertiseData(): AdvertiseData {

        val dataBuilder = AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(SERVICE_UUID))
            .setIncludeDeviceName(true)

        /* For example - this will cause advertising to fail (exceeds size limit) */
        //String failureData = "asdghkajsghalkxcjhfa;sghtalksjcfhalskfjhasldkjfhdskf";
        //dataBuilder.addServiceData(Constants.Service_UUID, failureData.getBytes());
        return dataBuilder.build()
    }

    private fun buildAdvertiseSettings(): AdvertiseSettings {
        return AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setTimeout(0)
            .build()
    }

    private class GattServerCallback : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            val isSuccess = status == BluetoothGatt.GATT_SUCCESS
            val isConnected = newState == BluetoothProfile.STATE_CONNECTED
            Log.d(TAG, "onConnectionStateChange: Server $device ${device.name} success: $isSuccess connected: $isConnected")

            if (isSuccess && isConnected) {
                deviceConnectionState.tryEmit(BleConnectionState.Connected(device))
            } else if (!isSuccess && !isConnected)
                deviceConnectionState.tryEmit(BleConnectionState.Disconnected)
            else
                deviceConnectionState.tryEmit(BleConnectionState.ConnectionFailed)

        }

        override fun onCharacteristicWriteRequest(device: BluetoothDevice, requestId: Int, characteristic: BluetoothGattCharacteristic, preparedWrite: Boolean, responseNeeded: Boolean, offset: Int, value: ByteArray?) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value)

            if (characteristic.uuid == MESSAGE_UUID) {
                gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null)
                val message = value?.toString(Charsets.UTF_8)
                Log.e(TAG, "onCharacteristicWriteRequest: Have message: \"$message\"")
                message?.let {
                    Log.e(TAG, "fromJson: $it")
                    val response = Gson().fromJson(it, BleResponse::class.java)

//                    if (response.type == ResponseType.REQUEST_PROFILE.value) {
////                        val profile = repository.getProfile()
//                        val profileModel = ProfileModel(userID = "profile.id", name = "profile.name")
//                        val bleResponse = BleResponse(type = ResponseType.PROFILE.value, profile = profileModel)
//                        sendMessage(bleResponse)
//                    } else
                        receivedMessage.value = response
                }
            }
        }
    }

    private class GattClientCallback : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            val isSuccess = status == BluetoothGatt.GATT_SUCCESS
            val isConnected = newState == BluetoothProfile.STATE_CONNECTED
            Log.d(TAG, "onConnectionStateChange: Client $gatt  success: $isSuccess connected: $isConnected")

            // try to send a message to the other device as a test
            if (isSuccess && isConnected) {
                // discover services
                gatt.requestConnectionPriority(CONNECTION_PRIORITY_HIGH)
                gatt.requestMtu(400)
//                gatt.discoverServices()
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            Log.d(TAG, "onMtuChanged $mtu")

            gatt!!.discoverServices()
        }

        override fun onServicesDiscovered(discoveredGatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(discoveredGatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "onServicesDiscovered: Have gatt $discoveredGatt")
                gatt = discoveredGatt
                val service = discoveredGatt.getService(SERVICE_UUID)
                messageCharacteristic = service.getCharacteristic(MESSAGE_UUID)
            }
        }
    }

    /**
     * Custom callback after Advertising succeeds or fails to start. Broadcasts the error code
     * in an Intent to be picked up by AdvertiserFragment and stops this Service.
     */
    private class DeviceAdvertiseCallback : AdvertiseCallback() {
        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            // Send error state to display
            val errorMessage = "Advertise failed with error: $errorCode"
            Log.d(TAG, "Advertising failed")
            //_viewState.value = DeviceScanViewState.Error(errorMessage)
        }
    }
}