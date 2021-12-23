package uk.fernando.bluetoothtalk.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import uk.fernando.bluetoothtalk.components.BottomNavigationBar
import uk.fernando.bluetoothtalk.navigation.Directions
import uk.fernando.bluetoothtalk.navigation.buildGraph
import uk.fernando.bluetoothtalk.service.ServiceBinderLifecycleObserver
import uk.fernando.bluetoothtalk.theme.MyTheme

@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val serviceObserver = ServiceBinderLifecycleObserver(this)

    init {
        lifecycle.addObserver(serviceObserver)
    }


    private fun startBle() {

//        binder.apply {
////            this?.connect("")
//            this?.bluetoothOn()
//        }

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this , arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                1
//            )
//        }


//        if (bluetoothManager == null) {
//            Log.e(TAG, "startBle: nulllllllll", )
//            // Device doesn't support Bluetooth
//            return
//        }


        //make your device be discovered
//        if(!bluetoothManager.adapter.isDiscovering){
//            val requestCode = 1;
//            val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
//                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
//            }
//            startActivityForResult(discoverableIntent, requestCode)
//        }


        // Register for broadcasts when a device is discovered.
//        val filter = IntentFilter()
//
//        filter.addAction(BluetoothDevice.ACTION_FOUND)
////        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
////        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
//
//        registerReceiver(receiver, filter)
//
//        if (bluetoothManager?.isDiscovering == true)
//            bluetoothManager?.cancelDiscovery()
//
//       val boo =  bluetoothManager?.startDiscovery()


//        Log.e(TAG, "startBle: aiii $boo")
//        if (bluetoothManager.adapter?.isEnabled == true) {
//            val devices = bluetoothManager.adapter?.bondedDevices
//
//            devices?.forEach { device ->
//                val deviceName = device?.name
//                Log.e("***1", "***** $deviceName")
//            }
//        }

    }

//    // Receiver for enable bluetooth
//    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//        if (it.resultCode == Activity.RESULT_OK) {
//            val value = it.data?.getStringExtra("input")
//        }
//    }
//
//    // Create a BroadcastReceiver for ACTION_FOUND.
//    private val receiver = object : BroadcastReceiver() {
//
//        override fun onReceive(context: Context, intent: Intent) {
//            val action: String? = intent.action
//            Log.e("***", "action: ***** ${action}")
//            when (action) {
//                BluetoothDevice.ACTION_FOUND -> {
//                    // Discovery has found a device. Get the BluetoothDevice
//                    // object and its info from the Intent.
//                    Log.e("***", "ACTION_FOUND")
//                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
//                    val deviceName = device?.type
//                    Log.e("***", "onReceive: ***** $deviceName")
//                    val deviceHardwareAddress = device?.address // MAC address
//                }
//                ACTION_DISCOVERY_STARTED -> {
//                    //discovery starts, we can show progress dialog or perform other tasks
//                    Log.e("***", "ACTION_DISCOVERY_STARTED")
//                }
//                ACTION_DISCOVERY_FINISHED -> {
//                    //discovery finishes, dismiss progress dialog
//                    Log.e("***", "ACTION_DISCOVERY_FINISHED")
//                }
//            }
//        }
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val controller = rememberNavController()
            val navBackStackEntry by controller.currentBackStackEntryAsState()

            MyTheme {

                Scaffold(
                    bottomBar = {
                        when (navBackStackEntry?.destination?.route) {
                            Directions.bluetooth.name, Directions.chat.name, Directions.settings.name ->
                                BottomNavigationBar(controller)
                        }
                    }
                ) { padding ->

                    Box(modifier = Modifier.padding(padding)) {
                        NavHost(
                            navController = controller,
                            startDestination = Directions.bluetooth.name
                        ) {
                            buildGraph(controller)
                        }
                    }

                }
            }
        }
    }
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}