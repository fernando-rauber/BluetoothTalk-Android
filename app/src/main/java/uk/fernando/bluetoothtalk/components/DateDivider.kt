package uk.fernando.bluetoothtalk.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.fernando.bluetoothtalk.ext.formatToDate
import java.util.*

@Preview(showBackground = true)
@Composable
fun DateDivider(date: Date = Date()) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Divider(Modifier.weight(0.5f))

        Text(
            text = date.formatToDate(),
            fontSize = 12.sp,
            maxLines = 1,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 5.dp)
        )

        Divider(Modifier.weight(0.5f))
    }
}