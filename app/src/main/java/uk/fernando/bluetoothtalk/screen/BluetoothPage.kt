package uk.fernando.bluetoothtalk.screen

import android.bluetooth.BluetoothDevice
import android.content.Context.LOCATION_SERVICE
import android.location.LocationManager
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uk.fernando.bluetoothtalk.R
import uk.fernando.bluetoothtalk.components.CustomSwitch
import uk.fernando.bluetoothtalk.ext.checkLocationPermission
import uk.fernando.bluetoothtalk.navigation.Directions
import uk.fernando.bluetoothtalk.theme.blueDark
import uk.fernando.bluetoothtalk.theme.greyDark
import uk.fernando.bluetoothtalk.viewmodel.BluetoothViewModel


@ExperimentalMaterialApi
@Composable
fun BluetoothPage(navController: NavController = NavController(LocalContext.current), viewModel: BluetoothViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetElevation = 8.dp,
        sheetContent = { Text(text = "GPS needs to be enable") }) {

        Column(Modifier.fillMaxSize()) {

            // Bluetooth Switch
            CustomSwitch(modifier = Modifier.padding(top = 10.dp),
                text = R.string.bluetooth_action,
                isChecked = viewModel.isBluetoothOn,
                onCheckedChange = { isON ->
                    viewModel.enableDisableBle()
                    viewModel.isBluetoothOn = isON
                })

            if (viewModel.isBluetoothOn) {

                ScanButton(
                    navController = navController,
                    onClick = viewModel::startScan,
                    isScanning = viewModel.isScanning.value,
                    showDialog = {
                        coroutineScope.launch {
                            sheetState.show()
                        }
                    }
                )

                // Lists
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    DeviceList(
                        textId = R.string.my_devices,
                        deviceList = viewModel.myDevices.value
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    DeviceList(
                        textId = R.string.other_devices,
                        deviceList = viewModel.otherDevices.value
                    )

                    if (viewModel.devicesNotFound.value)
                        DeviceListNotFound()
                }
            }
        }
    }
}

@Composable
private fun ScanButton(navController: NavController, onClick: () -> Unit, showDialog: () -> Unit, isScanning: Boolean) {
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current
    val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager?

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {

        if (isScanning)
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                strokeWidth = 3.dp
            )

        TextButton(modifier = Modifier
            .padding(end = 15.dp)
            .align(Alignment.CenterEnd),
            enabled = !isScanning,
            onClick = {
                if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    showDialog()
                } else
                    coroutine.launch {
                        context.checkLocationPermission(
                            onGranted = onClick,
                            onNotGranted = {
                                navController.navigate((Directions.locationPermission.name))
                            })
                    }
            }
        ) {
            Text(
                modifier = Modifier.padding(vertical = 5.dp),
                text = stringResource(id = R.string.scan_action).uppercase(),
                color = if (!isScanning) blueDark else greyDark,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun DeviceList(@StringRes textId: Int, deviceList: List<BluetoothDevice>) {

    if (deviceList.isNotEmpty()) {
        Text(
            modifier = Modifier.padding(start = 16.dp, bottom = 5.dp),
            text = stringResource(id = textId),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Surface(shape = RoundedCornerShape(18.dp)) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                deviceList.forEachIndexed { index, device ->
                    DeviceCard(device.name)

                    if (deviceList.count().minus(1) != index)
                        Divider(Modifier.padding(start = 30.dp))
                }
            }
        }
    }
}

@Composable
private fun DeviceCard(name: String) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {

        Icon(painter = painterResource(id = R.drawable.ic_smartphone), contentDescription = "Smartphone")

        Column(Modifier.padding(start = 10.dp)) {
            Text(text = name, fontSize = 18.sp)
//            Text(
//                modifier = Modifier.padding(top = 5.dp),
//                text = device.address,
//                fontSize = 14.sp,
//                color = grey
//            )
        }

//        if (device.isConnected) {
//            Spacer(modifier = Modifier.weight(1f))
//
//            Text(
//                text = stringResource(id = R.string.connected),
//                fontSize = 16.sp,
//                color = green
//            )
//        }
    }
}

@Composable
private fun DeviceListNotFound() {

    Text(
        modifier = Modifier.padding(start = 16.dp, bottom = 5.dp),
        text = stringResource(id = R.string.other_devices),
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )

    Surface(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        DeviceCard(stringResource(id = R.string.devices_not_found))

    }
}
