package uk.fernando.bluetoothtalk.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.fernando.bluetoothtalk.theme.blue
import uk.fernando.bluetoothtalk.theme.greySuperLight

@Composable
fun CustomDeviceCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = true,
    color: Color = blue,
    textColor: Color = Color.White,
    fontSize: TextUnit = 16.sp,
    textModifier: Modifier = Modifier,
    borderStroke: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    elevation: ButtonElevation = ButtonDefaults.elevation(0.dp, 0.dp)
) {
    Button(
        border = borderStroke,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(percent = 50),
        colors = ButtonDefaults.buttonColors(backgroundColor = color, disabledBackgroundColor = greySuperLight),
        elevation = elevation,
        contentPadding = contentPadding,
        onClick = { onClick() }
    ) {
        Text(
            modifier = textModifier,
            text = text,
            textAlign = TextAlign.Center,
            color = if (enabled) textColor else Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize
        )
    }
}
