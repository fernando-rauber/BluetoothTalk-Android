package uk.fernando.bluetoothtalk.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import uk.fernando.bluetoothtalk.R
import uk.fernando.bluetoothtalk.ext.checkBluetoothPermission
import uk.fernando.bluetoothtalk.navigation.Directions
import uk.fernando.bluetoothtalk.theme.blue
import uk.fernando.bluetoothtalk.theme.greyLight4


@Composable
fun BottomNavigationBar(navController: NavController) {
    val context = LocalContext.current

    Box {

        Surface(
            modifier = Modifier
                .shadow(5.dp)
                .align(Alignment.BottomCenter),
            elevation = 16.dp,
            shape = MaterialTheme.shapes.medium.copy(topStart = CornerSize(16.dp), topEnd = CornerSize(16.dp))
        ) {

            BottomNavigation(
                modifier = Modifier,
                backgroundColor = blue
            ) {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                NavigationItemCustom(currentRoute == Directions.bluetooth.name, R.drawable.ic_bluetooth, R.string.bluetooth_action) {
                    if (currentRoute != Directions.bluetooth.name)
                        context.checkBluetoothPermission(
                            onGranted = { navController.navigate(Directions.bluetooth.name) },
                            onNotGranted = { navController.navigate((Directions.bluetoothPermission.name)) })
                }

                NavigationItemCustom(currentRoute == Directions.chatList.name, R.drawable.ic_chat, R.string.chat_action) {
                    if (currentRoute != Directions.chatList.name)
                        navController.navigate(Directions.chatList.name)
                }

                NavigationItemCustom(currentRoute == Directions.settings.name, R.drawable.ic_settings, R.string.settings_action) {
                    if (currentRoute != Directions.settings.name)
                        navController.navigate(Directions.settings.name)
                }
            }
        }
    }
}


@Composable
fun RowScope.NavigationItemCustom(
    isSelected: Boolean,
    @DrawableRes iconID: Int,
    @StringRes stringID: Int,
    onClick: () -> Unit
) {
    BottomNavigationItem(
        icon = {
            Icon(
                painter = painterResource(id = iconID),
                contentDescription = null
            )
        },
        selectedContentColor = Color.White,
        unselectedContentColor = greyLight4,
        selected = isSelected,
        label = {
            Text(
                text = stringResource(id = stringID),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = if (isSelected) Color.White else greyLight4
            )
        },
        onClick = onClick
    )
}