package uk.fernando.bluetoothtalk.screen

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import uk.fernando.bluetoothtalk.activity.Greeting

@Preview(showBackground = true)
@Composable
fun ChatPage(navController: NavController = NavController(LocalContext.current)) {
    // A surface container using the 'background' color from the theme
    Surface(color = MaterialTheme.colors.background) {
        Greeting("Chat Page")
    }
}