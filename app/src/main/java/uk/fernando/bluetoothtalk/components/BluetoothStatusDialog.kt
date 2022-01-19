package uk.fernando.bluetoothtalk.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.fernando.bluetoothtalk.R

@Composable
fun BluetoothStatusDialog(modifier: Modifier, list: List<String>) {
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .then(modifier)
    ) {
        Column(
            Modifier
                .padding(top = 30.dp, bottom = 10.dp)
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            list.forEach { text ->
                Text(
                    text = text,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
            }
        }
    }

}