package uk.fernando.bluetoothtalk.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

interface NavigationCommand {
    val name: String
    val arguments: List<NamedNavArgument>
}

object Directions {

    val splash = object : NavigationCommand {
        override val name: String
            get() = "splash"
        override val arguments: List<NamedNavArgument>
            get() = emptyList()
    }

    val bluetooth = object : NavigationCommand {
        override val name: String
            get() = "bluetooth"
        override val arguments: List<NamedNavArgument>
            get() = emptyList()
    }

    val chatList = object : NavigationCommand {
        override val name: String
            get() = "chat_list"
        override val arguments: List<NamedNavArgument>
            get() = emptyList()
    }

    val chat = object : NavigationCommand {
        override val name: String
            get() = "chat"
        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(USER_ADDRESS) { type = NavType.StringType }
            )
    }

    val settings = object : NavigationCommand {
        override val name: String
            get() = "settings"
        override val arguments: List<NamedNavArgument>
            get() = emptyList()
    }

    val locationPermission = object : NavigationCommand {
        override val name: String
            get() = "location_permission"
        override val arguments: List<NamedNavArgument>
            get() = emptyList()
    }

    val bluetoothPermission = object : NavigationCommand {
        override val name: String
            get() = "bluetooth_permission"
        override val arguments: List<NamedNavArgument>
            get() = emptyList()
    }

    const val USER_ADDRESS = "user_address"
}


