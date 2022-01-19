package uk.fernando.bluetoothtalk.screen.permission

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uk.fernando.bluetoothtalk.R
import uk.fernando.bluetoothtalk.components.CustomButton
import uk.fernando.bluetoothtalk.theme.blue
import uk.fernando.bluetoothtalk.theme.buttonTextStyle

@Composable
fun PermissionPageContent(
    permission: Permission,
    onPositive: () -> Unit = {},
    onNegative: () -> Unit = {}
) {
    Column(modifier = Modifier.background(Color.White)) {
        Spacer(Modifier.weight(.1f))
        Image(
            modifier = Modifier
                .weight(1f)
//                    .padding(top = 70.dp)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(id = permission.image),
            contentDescription = null,
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.weight(.1f))
        Text(
            text = permission.title,
            modifier = Modifier.padding(
                start = 30.dp,
                end = 30.dp,
//                    top = 10.dp,
                bottom = 18.dp
            ),
            style = TextStyle.Default.copy(
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
        )
        Text(
            text = stringResource(id = permission.message),
            modifier = Modifier.padding(
                start = 30.dp,
                end = 30.dp,
                top = 18.dp,
                bottom = 28.dp
            ),
            style = TextStyle.Default.copy(
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
            )
        )
        Row(
            modifier = Modifier
                .padding(
                    bottom = 43.dp,
                    start = 23.dp,
                    end = 27.dp
                )
                .fillMaxWidth()
        ) {
            TextButton(
                modifier = Modifier.padding(end = 4.dp),
                onClick = onNegative
            ) {
                Text(
                    text = stringResource(id = R.string.maybe_later_action),
                    style = buttonTextStyle.copy(
                        textAlign = TextAlign.Start
                    )
                )
            }

            Spacer(Modifier.weight(1f))

            CustomButton(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .defaultMinSize(minHeight = 40.dp),
                onClick = onPositive,
                color = blue,
                text = stringResource(id = R.string.sure_ask_permission_action).uppercase(),
                textColor = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun LocationPermissionPage(navController: NavController = NavController(LocalContext.current)) {

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val context = LocalContext.current

    val activityResult = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            coroutineScope.launch {
                navController.popBackStack()
            }
        } else {
            coroutineScope.launch {
                sheetState.show()
            }
        }
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
                text = R.string.permission_denied_location_message,
                onDismiss = onDismiss,
                onSettingsClick = onSettingsClick
            )
        },
    ) {
        PermissionPageContent(
            Permission(
                image = R.drawable.img_smartphone,
                buildAnnotatedString {
                    withStyle(SpanStyle(color = blue)) {
                        append(stringResource(id = R.string.app_name))
                    }
                    val text = stringResource(R.string.permission_priming_location_title)
                    append(text)
                },
                R.string.permission_priming_location_message
            ),
            onNegative = {
                coroutineScope.launch {
                    sheetState.show()
                }
            },
            onPositive = {
//                if ((context as Activity).shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
//                    coroutineScope.launch {
//                        sheetState.show()
//                    }
//                } else {
                    activityResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    // not accepted yet
//                }
            }
        )
    }

}

class Permission(
    @DrawableRes val image: Int,
    val title: AnnotatedString,
    @StringRes val message: Int,
)
