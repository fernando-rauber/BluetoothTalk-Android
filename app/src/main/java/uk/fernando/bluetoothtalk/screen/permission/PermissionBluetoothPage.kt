package uk.fernando.bluetoothtalk.screen.permission

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.koin.androidx.compose.inject
import uk.fernando.bluetoothtalk.R
import uk.fernando.bluetoothtalk.service.ble.ChatServer
import uk.fernando.bluetoothtalk.theme.blue
import uk.fernando.bluetoothtalk.theme.green
import uk.fernando.bluetoothtalk.theme.red

@SuppressLint("InlinedApi")
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun BluetoothPermissionPage(navController: NavController = NavController(LocalContext.current)) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val context = LocalContext.current

    val bleScanResult = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            ChatServer.startServer(context)
            coroutineScope.launch { navController.popBackStack() }
        } else
            coroutineScope.launch { sheetState.show() }
    }
    val bleConnectResult = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted)
            bleScanResult.launch(Manifest.permission.BLUETOOTH_SCAN)
        else
            coroutineScope.launch { sheetState.show() }
    }
    val bleAdvertiseResult = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted)
            bleConnectResult.launch(Manifest.permission.BLUETOOTH_CONNECT)
        else
            coroutineScope.launch { sheetState.show() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetElevation = 8.dp,
        sheetContent = {
            val onDismiss: () -> Unit = {
                coroutineScope.launch {
                    sheetState.hide()
                }
            }
            val onSettingsClick = {
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                        Uri.fromParts("package", context.applicationContext.packageName, null)
                    )
                )
            }
            PermissionDeniedSheet(
                text = R.string.permission_denied_bluetooth_message,
                onDismiss = onDismiss,
                onSettingsClick = onSettingsClick
            )
        },
    ) {
        PermissionPageContent(
            Permission(
                image = R.drawable.img_bluetooth,
                buildAnnotatedString {
                    withStyle(SpanStyle(color = blue)) {
                        append(stringResource(id = R.string.app_name))
                    }
                    val text = stringResource(R.string.permission_priming_bluetooth_title)
                    append(text)
                },
                body = {
                    BlePermissions(context)
                },
            ),
            onNegative = {
                coroutineScope.launch {
                    sheetState.show()
                }
            },
            onPositive = {
                bleAdvertiseResult.launch(Manifest.permission.BLUETOOTH_ADVERTISE)
            }
        )
    }
}


@SuppressLint("InlinedApi")
@Composable
private fun BlePermissions(context: Context) {
    val bleAdvertise = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED
    val bleConnect = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
    val bleScan = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        PermissionItem(bleAdvertise, R.string.permission_advertise)

        PermissionItem(bleConnect, R.string.permission_connect)

        PermissionItem(bleScan, R.string.permission_scan)
    }
}

@Composable
private fun PermissionItem(isGranted: Boolean, @StringRes text: Int) {

    Text(
        text = stringResource(id = text),
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(vertical = 10.dp, horizontal = 30.dp)
    )

    Surface(
        modifier = Modifier.fillMaxWidth(0.8f),
        elevation = 4.dp,
        color = if (isGranted) green else red,
        shape = RoundedCornerShape(percent = 25)
    ) {

        Text(
            text = stringResource(id = if (isGranted) R.string.permission_granted else R.string.permission_no_granted),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 30.dp)
        )
    }
}