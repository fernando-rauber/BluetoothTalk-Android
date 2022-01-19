package uk.fernando.bluetoothtalk.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.fernando.bluetoothtalk.theme.dark
import uk.fernando.bluetoothtalk.theme.steel

@Preview(showBackground = true, showSystemUi = false, backgroundColor = 0xFFFFFFFF)
@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    header: String? = "header",
    value: String = "",
    onValueChange: (String) -> Unit = {},
    headerTextStyle: TextStyle = TextStyle.Default,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    label: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = TextStyle.Default,
    isError: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false
) {
    Column(modifier = modifier) {
        if (header != null)
            Text(
                text = header,
                style = textStyle,
                modifier = Modifier.fillMaxWidth()
            )
        OutlinedTextField(
            value = value,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp),
            onValueChange = onValueChange,
            visualTransformation = visualTransformation,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = Color.Transparent,
                unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                focusedBorderColor = MaterialTheme.colors.primary,
                cursorColor = MaterialTheme.colors.primary
            ),
            enabled = enabled,
            label = label,
            readOnly = readOnly,
            trailingIcon = trailingIcon,
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions,
            textStyle = textStyle,
            isError = isError,
            maxLines = maxLines,
            placeholder = {
                if (placeholder != null)
                    Text(
                        text = placeholder,
                        style = TextStyle.Default.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Light,
                            color = steel.copy(0.75f)
                        )
                    )
            },
            leadingIcon = leadingIcon,
            singleLine = singleLine,
        )
    }
}
