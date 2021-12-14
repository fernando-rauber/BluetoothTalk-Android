package uk.fernando.bluetoothtalk.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.fernando.bluetoothtalk.theme.blue
import uk.fernando.bluetoothtalk.theme.green
import uk.fernando.bluetoothtalk.theme.greyDark
import uk.fernando.bluetoothtalk.theme.greySuperLight

@Composable
fun CustomSwitch(
    modifier: Modifier = Modifier,
    @StringRes text: Int,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {

            Text(
                text = stringResource(id = text),
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 20.dp)
            )

            Surface(
                modifier = Modifier.height(30.dp),
                color = if (isChecked) blue else greySuperLight,
                shape = RoundedCornerShape(15.dp)
            ) {
                Switch(
                    modifier = Modifier
                        .padding(6.dp)
                        .scale(1.2f),
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = blue,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = greySuperLight
                    )
                )
            }

        }
    }
}
