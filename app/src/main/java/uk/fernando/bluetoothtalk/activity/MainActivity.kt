package uk.fernando.bluetoothtalk.activity

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import uk.fernando.bluetoothtalk.components.BottomNavigationBar
import uk.fernando.bluetoothtalk.navigation.Directions
import uk.fernando.bluetoothtalk.navigation.buildGraph
import uk.fernando.bluetoothtalk.theme.MyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val controller = rememberNavController()
            MyTheme {

                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(controller)
                    }
                ) { innerPadding ->
                    val direction =
                        if (LocalConfiguration.current.layoutDirection == View.LAYOUT_DIRECTION_LTR) LayoutDirection.Ltr else LayoutDirection.Rtl
                    //reduced padding from the bottom to prevent the content from being cut
                    val padding =
                        PaddingValues(
                            start = innerPadding.calculateStartPadding(direction),
                            end = innerPadding.calculateEndPadding(direction),
                            top = innerPadding.calculateTopPadding(),
                            bottom = (innerPadding.calculateBottomPadding() - 20.dp).coerceAtLeast(
                                0.dp
                            )
                        )
                    // Apply the padding globally to the whole BottomNavScreensContent
                    Box(modifier = Modifier) {
                        NavHost(
                            navController = controller,
                            startDestination = Directions.bluetooth.name
                        ) {
                            buildGraph(controller)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}