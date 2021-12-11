package uk.fernando.bluetoothtalk.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uk.fernando.bluetoothtalk.activity.Greeting
import uk.fernando.bluetoothtalk.navigation.Directions

@Preview(showBackground = true)
@Composable
fun BluetoothPage(navController: NavController = NavController(LocalContext.current)) {
    // A surface container using the 'background' color from the theme
    Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
        Greeting("BluetoothPage Page")


    }
}