package uk.fernando.bluetoothtalk.screen.permission

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.fernando.bluetoothtalk.R
import uk.fernando.bluetoothtalk.components.CustomButton
import uk.fernando.bluetoothtalk.theme.blue
import uk.fernando.bluetoothtalk.theme.buttonTextStyle

@Composable
fun PermissionPageContent(
    permission: Permission,
    onPositive: () -> Unit = {},
    onNegative: () -> Unit = {}
) {
    Column {
        Spacer(Modifier.weight(.1f))
        Image(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(id = permission.image),
            contentDescription = null,
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.weight(.1f))
        Text(
            text = permission.title,
            modifier = Modifier.padding(
                start = 30.dp,
                end = 30.dp,
                bottom = 18.dp
            ),
            style = TextStyle.Default.copy(
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
            )
        )

        permission.body?.let { it() }

        permission.message?.let { message ->
            Text(
                text = stringResource(id = message),
                modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 18.dp),
                style = TextStyle.Default.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                )
            )
        }

        Row(
            modifier = Modifier
                .padding(bottom = 43.dp, start = 23.dp, end = 27.dp, top = 28.dp)
                .fillMaxWidth()
        ) {
            TextButton(
                modifier = Modifier.padding(end = 4.dp),
                onClick = onNegative
            ) {
                Text(
                    text = stringResource(id = R.string.maybe_later_action),
                    style = buttonTextStyle.copy(
                        textAlign = TextAlign.Start
                    )
                )
            }

            Spacer(Modifier.weight(1f))

            CustomButton(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .defaultMinSize(minHeight = 40.dp),
                onClick = onPositive,
                color = blue,
                text = stringResource(id = R.string.sure_ask_permission_action).uppercase(),
                textColor = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

class Permission(
    @DrawableRes val image: Int,
    val title: AnnotatedString,
    val body: @Composable (() -> Unit)? = null,
    @StringRes val message: Int? = null,
)
