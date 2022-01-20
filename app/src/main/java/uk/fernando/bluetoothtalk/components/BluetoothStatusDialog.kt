package uk.fernando.bluetoothtalk.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import uk.fernando.bluetoothtalk.R
import uk.fernando.bluetoothtalk.navigation.Directions
import uk.fernando.bluetoothtalk.service.ble.BleConnectionState
import uk.fernando.bluetoothtalk.service.ble.ChatServer
import uk.fernando.bluetoothtalk.service.model.BleResponse
import uk.fernando.bluetoothtalk.service.model.ProfileModel
import uk.fernando.bluetoothtalk.service.model.ResponseType
import uk.fernando.bluetoothtalk.theme.red

@Composable
fun BluetoothStatusDialog(modifier: Modifier, list: List<BleConnectionState>, onDisconnect: () -> Unit) {
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(0.8f)
            .then(modifier)
    ) {
        Column(
            Modifier
                .padding(top = 30.dp, bottom = 10.dp)
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            list.forEach { status ->
                Text(
                    text = getText(status),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 15.dp)
                )

                if (status is BleConnectionState.Disconnected) {
                    CustomButton(
                        onClick = onDisconnect,
                        text = stringResource(id = R.string.close),
                        color = red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp)
                            .defaultMinSize(minHeight = 50.dp),
                    )
                }
            }
        }
    }
}

private fun getText(status: BleConnectionState): String {
    return when (status) {
        is BleConnectionState.Connecting -> "Connecting"
        is BleConnectionState.Connected -> {
            "Connected to ${status.device}\n" +
                    "Requesting device ${status.device}'s connection"
        }
        is BleConnectionState.GotConnectedBy -> "Got connected by: ${status.device}"
        is BleConnectionState.Disconnected -> "Disconnected"
        is BleConnectionState.ConnectionEstablished -> "Connection Established"
    }
}