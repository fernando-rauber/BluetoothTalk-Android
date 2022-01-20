package uk.fernando.bluetoothtalk.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uk.fernando.bluetoothtalk.navigation.Directions.USER_ADDRESS
import uk.fernando.bluetoothtalk.screen.BluetoothPage
import uk.fernando.bluetoothtalk.screen.ChatListPage
import uk.fernando.bluetoothtalk.screen.ChatPage
import uk.fernando.bluetoothtalk.screen.SettingsPage
import uk.fernando.bluetoothtalk.screen.permission.LocationPermissionPage


@ExperimentalAnimationApi
@ExperimentalMaterialApi
fun NavGraphBuilder.buildGraph(navController: NavController) {
    composable(Directions.splash.name) {

    }
    composable(Directions.bluetooth.name) {
        BluetoothPage(navController)
    }
    composable(Directions.chatList.name) {
        ChatListPage(navController)
    }
    composable(Directions.settings.name) {
        SettingsPage(navController)
    }
    composable(Directions.chat.name.plus("/{$USER_ADDRESS}")) {
        val address = it.arguments?.getString(USER_ADDRESS)
        if (address.isNullOrEmpty())
            navController.popBackStack()
        else
            ChatPage(navController, userAddress = address)
    }
    composable(Directions.locationPermission.name) {
        LocationPermissionPage(navController)
    }
}


