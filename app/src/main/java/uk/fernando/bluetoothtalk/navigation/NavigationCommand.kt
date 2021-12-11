package uk.fernando.bluetoothtalk.navigation

import androidx.navigation.NamedNavArgument

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

    val chat = object : NavigationCommand {
        override val name: String
            get() = "chat"
        override val arguments: List<NamedNavArgument>
            get() = emptyList()
    }

    val settings = object : NavigationCommand {
        override val name: String
            get() = "settings"
        override val arguments: List<NamedNavArgument>
            get() = emptyList()
    }
}


