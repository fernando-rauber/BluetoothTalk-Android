package uk.fernando.bluetoothtalk.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uk.fernando.bluetoothtalk.screen.*


fun NavGraphBuilder.buildGraph(navController: NavController) {
    composable(Directions.splash.name) {

    }
    composable(Directions.bluetooth.name) {
        BluetoothPage(navController)
    }
    composable(Directions.chat.name) {
        ChatPage(navController)
    }
    composable(Directions.settings.name) {
        SettingsPage(navController)
    }
}


