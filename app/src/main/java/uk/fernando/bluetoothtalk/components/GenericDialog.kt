package uk.fernando.bluetoothtalk.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import uk.fernando.bluetoothtalk.R

@Composable
fun GenericDialog(modifier: Modifier = Modifier, onDismiss: (() -> Unit), content: @Composable BoxScope.() -> Unit) {
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .then(modifier)
    ) {
        Box {
            content(this)

//            IconButton(
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .padding(end = 10.dp, top = 10.dp),
//                onClick = onDismiss
//            ) {
//                Icon(
//                    modifier = Modifier.align(Alignment.Center),
//                    painter = painterResource(id = R.drawable.ic_cancel),
//                    contentDescription = null,
//                    tint = Color.Unspecified,
//                )
//            }
        }
    }
}