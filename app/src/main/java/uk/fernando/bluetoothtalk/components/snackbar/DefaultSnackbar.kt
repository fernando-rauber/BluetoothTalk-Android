package uk.fernando.bluetoothtalk.components.snackbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
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
fun CreateSnackBar(backgroundColor: Color, message: String) {
    Snackbar(
        shape = MaterialTheme.shapes.large.copy(CornerSize(0.dp)),
        backgroundColor = backgroundColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            // Message
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 15.dp, bottom = 10.dp)
            ) {
                Text(
                    text = message,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White
                )
            }

        }
    }
}

