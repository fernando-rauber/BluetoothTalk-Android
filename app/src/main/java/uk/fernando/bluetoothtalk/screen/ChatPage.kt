package uk.fernando.bluetoothtalk.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import uk.fernando.bluetoothtalk.R
import uk.fernando.bluetoothtalk.database.entity.MessageEntity
import uk.fernando.bluetoothtalk.database.entity.UserEntity
import uk.fernando.bluetoothtalk.theme.blue
import uk.fernando.bluetoothtalk.theme.green
import uk.fernando.bluetoothtalk.theme.greyDark
import uk.fernando.bluetoothtalk.theme.greyLight2
import uk.fernando.bluetoothtalk.viewmodel.ChatViewModel

@Preview(showBackground = true)
@Composable
fun ChatPage(navController: NavController = NavController(LocalContext.current), userAddress: String = "", viewModel: ChatViewModel = getViewModel()) {
    viewModel.fetchMessages(userAddress)
    val coroutine = rememberCoroutineScope()
    val listState = rememberLazyListState()

    Column(Modifier.fillMaxSize()) {

        TopBar(
            user = viewModel.user.value,
            onBackClick = { navController.popBackStack() }
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(0.9f)
        ) {

            items(viewModel.messageList.value) { message ->
                ChatDialog(message)
            }
        }

        BottomBar(onSendMessage = {
            coroutine.launch {
                viewModel.sendMessage(it)
                listState.animateScrollToItem(viewModel.messageList.value.count() - 1)
            }
        })

        // Scroll to last message on the chat
        coroutine.launch {
            listState.animateScrollToItem(viewModel.messageList.value.count() - 1)
        }
    }
}

@Composable
private fun TopBar(user: UserEntity?, onBackClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        elevation = 8.dp
    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                    tint = greyDark
                )
            }

            // Image
            Image(
                modifier = Modifier.size(35.dp),
                painter = if (user == null || user.image.isNullOrEmpty()) painterResource(id = R.drawable.img_no_avatar) else rememberImagePainter(user.image, builder = {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            // Text - User name
            Text(
                text = user?.name ?: "",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 5.dp),
            )
        }
    }
}

@Composable
private fun BottomBar(onSendMessage: (String) -> Unit) {

    var message by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {

        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            TextField(
                value = message,
                modifier = Modifier
                    .weight(0.9f)
                    .defaultMinSize(minHeight = 40.dp)
                    .background(greyLight2.copy(0.3f), shape = RoundedCornerShape(20)),
                onValueChange = { message = it },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black,
                    backgroundColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    cursorColor = MaterialTheme.colors.primary,
                    placeholderColor = greyDark
                ),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.message),
                        style = TextStyle.Default.copy(fontSize = 16.sp, fontWeight = FontWeight.Normal)
                    )
                }
            )

            // Send Button
            IconButton(
                onClick = {
                    onSendMessage(message)
                    message = ""
                },
                enabled = message.isNotEmpty()
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_send),
                    contentDescription = null,
                    tint = if (message.isNotEmpty()) blue else greyDark
                )
            }
        }
    }
}

@Composable
private fun ChatDialog(message: MessageEntity) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 10.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .align(if (message.byMe) Alignment.CenterEnd else Alignment.CenterStart),
            horizontalAlignment = if (message.byMe) Alignment.End else Alignment.Start,
        ) {

            Surface(
                elevation = 2.dp,
                color = if (message.byMe) green else blue,
                shape = getRoundedCorner(message.byMe)
            ) {
                Text(
                    text = message.message,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

    }
}

private fun getRoundedCorner(byMe: Boolean): Shape {
    return if (byMe)
        RoundedCornerShape(30, 30, 0, 30)
    else
        RoundedCornerShape(30, 30, 30, 0)
}
