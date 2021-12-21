package uk.fernando.bluetoothtalk.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import uk.fernando.bluetoothtalk.model.Device
import uk.fernando.bluetoothtalk.navigation.Directions
import uk.fernando.bluetoothtalk.theme.blueDark
import uk.fernando.bluetoothtalk.theme.green
import uk.fernando.bluetoothtalk.theme.grey
import uk.fernando.bluetoothtalk.theme.red
import uk.fernando.bluetoothtalk.viewmodel.BluetoothViewModel

@Composable
fun BluetoothPage(navController: NavController = NavController(LocalContext.current), viewModel: BluetoothViewModel = hiltViewModel()) {

    Column(Modifier.fillMaxSize()) {

        CustomSwitch(modifier = Modifier.padding(top = 10.dp),
            text = R.string.bluetooth_action,
            isChecked = viewModel.isBluetoothOn,
            onCheckedChange = { viewModel.isBluetoothOn = it })

        if (viewModel.isBluetoothOn) {

            ScanButton(
                navController = navController,
                onClick = if (!viewModel.isScanning.value) viewModel::startScan else viewModel::cancelScan,
                isScanning = viewModel.isScanning.value
            )

            DeviceList(
                textId = R.string.my_devices,
                deviceList = viewModel.myDevices.value
            )

            Spacer(modifier = Modifier.height(20.dp))

            DeviceList(
                textId = R.string.other_devices,
                deviceList = viewModel.otherDevices.value
            )
        }
    }
}

@Composable
private fun ScanButton(navController: NavController, onClick: () -> Unit, isScanning: Boolean) {
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current

    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(Modifier.weight(1f))

        if (isScanning)
            CircularProgressIndicator(
                strokeWidth = 3.dp,
                modifier = Modifier.size(30.dp)
            )

        Spacer(Modifier.weight(0.7f))

        TextButton(
            modifier = Modifier
                .padding(top = 15.dp, end = 15.dp),
            onClick = {
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
                text = (if (!isScanning) stringResource(id = R.string.scan_action) else stringResource(id = R.string.cancel_action)).uppercase(),
                color = if (!isScanning) blueDark else red,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun DeviceList(@StringRes textId: Int, deviceList: List<Device>) {

    if (deviceList.isNotEmpty()) {
        Text(
            modifier = Modifier.padding(start = 16.dp, bottom = 5.dp),
            text = stringResource(id = textId),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )

        Surface(shape = RoundedCornerShape(18.dp)) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                itemsIndexed(deviceList) { index, device ->
                    DeviceCard(device)

                    if (deviceList.count().minus(1) != index)
                        Divider(Modifier.padding(start = 30.dp))
                }
            }
        }
    }
}

@Composable
private fun DeviceCard(device: Device) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {

        Icon(painter = painterResource(id = R.drawable.ic_smartphone), contentDescription = "Smartphone")

        Column(Modifier.padding(start = 10.dp)) {
            Text(text = device.name, fontSize = 18.sp)
            Text(
                modifier = Modifier.padding(top = 5.dp),
                text = device.address,
                fontSize = 14.sp,
                color = grey
            )
        }

        if (device.isConnected) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(id = R.string.connected),
                fontSize = 16.sp,
                color = green
            )
        }
    }
}