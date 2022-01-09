package uk.fernando.bluetoothtalk.components.snackbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.fernando.bluetoothtalk.theme.green
import uk.fernando.bluetoothtalk.theme.red

@Composable
fun DefaultSnackBar(
    snackBarHostState: SnackbarHostState,
    snackBarSealed: SnackBarSealed?,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = snackBarHostState,
        snackbar = {
            when (snackBarSealed) {
                is SnackBarSealed.Success -> CreateSnackBar(green, if (snackBarSealed.messageID != null) stringResource(id = snackBarSealed.messageID) else snackBarSealed.messageText ?: "")
                is SnackBarSealed.Error -> CreateSnackBar(red, if (snackBarSealed.messageID != null) stringResource(id = snackBarSealed.messageID) else snackBarSealed.messageText ?: "")
            }
        },
        modifier = modifier
    )
}

@Composable
private fun CreateSnackBar(backgroundColor: Color, message: String) {
    Snackbar(
        modifier = Modifier.padding(0.dp),
        shape = RoundedCornerShape(0,0,10,10),
        backgroundColor = backgroundColor
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            // Message
            Text(
                text = message,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 15.dp)
            )
        }


    }
}

