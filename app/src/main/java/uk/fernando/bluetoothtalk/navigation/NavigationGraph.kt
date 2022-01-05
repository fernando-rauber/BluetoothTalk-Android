package uk.fernando.bluetoothtalk.navigation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uk.fernando.bluetoothtalk.screen.*
import uk.fernando.bluetoothtalk.screen.permission.LocationPermissionPage


@ExperimentalMaterialApi
fun NavGraphBuilder.buildGraph(navController: NavController) {
    composable(Directions.splash.name) {

    }
    composable(Directions.bluetooth.name) {
        BluetoothPage(navController)
    }
    composable(Directions.chat.name) {
        ChatListPage(navController)
    }
    composable(Directions.settings.name) {
        SettingsPage(navController)
    }
    composable(Directions.locationPermission.name) {
        LocationPermissionPage(navController)
    }
}


