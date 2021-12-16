package uk.fernando.bluetoothtalk.screen.permission

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.fernando.bluetoothtalk.R
import uk.fernando.bluetoothtalk.components.CustomButton
import uk.fernando.bluetoothtalk.ext.htmlAnnotatedStringResource
import uk.fernando.bluetoothtalk.theme.blue

@Composable
fun PermissionDeniedSheet(
    @StringRes text: Int,
    onSettingsClick: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Column(
        Modifier
            .padding(
                top = 48.dp,
                start = 27.dp,
                end = 27.dp,
                bottom = 43.dp
            )
            .background(Color.White, AbsoluteCutCornerShape(topLeft = 10.dp, topRight = 10.dp))
            .wrapContentHeight()
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.permission_denied_title),
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Start
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = htmlAnnotatedStringResource(id = text),
            style = MaterialTheme.typography.body2
        )
        Row(Modifier.padding(top = 33.dp)) {
            CustomButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                fontSize = 14.sp,
                onClick = onDismiss,
                text = stringResource(id = R.string.okay_action).uppercase(),
                borderStroke = BorderStroke(2.dp, color = blue),
                color = Color.White,
                textColor = blue
            )
            CustomButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                fontSize = 14.sp,
                onClick = onSettingsClick,
                text = stringResource(id = R.string.app_settings_action).uppercase()
            )
        }
    }
}
