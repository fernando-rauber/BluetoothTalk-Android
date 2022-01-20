package uk.fernando.bluetoothtalk.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import org.koin.androidx.compose.getViewModel
import uk.fernando.bluetoothtalk.R
import uk.fernando.bluetoothtalk.components.AnimatedSwipeDelete
import uk.fernando.bluetoothtalk.database.entity.UserWithMessage
import uk.fernando.bluetoothtalk.ext.formatToChatDate
import uk.fernando.bluetoothtalk.ext.noRippleClickable
import uk.fernando.bluetoothtalk.navigation.Directions
import uk.fernando.bluetoothtalk.theme.greyDark
import uk.fernando.bluetoothtalk.theme.red
import uk.fernando.bluetoothtalk.viewmodel.ChatListViewModel

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun ChatListPage(navController: NavController = NavController(LocalContext.current), viewModel: ChatListViewModel = getViewModel()) {


    //No Messages Yet

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
    ) {

        items(viewModel.chatList.value, { listItem: UserWithMessage -> listItem.user.id }) { user ->
            AnimatedSwipeDelete(
                item = user,
                background = {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 15.dp, vertical = 7.dp)
                            .background(
                                color = red,
                                shape = MaterialTheme.shapes.medium.copy(CornerSize(15.dp))
                            )

                    ) {

                        Text(
                            text = stringResource(id = R.string.delete_action),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 15.dp)
                        )
                    }
                },
                content = {
                    UserChat(user = user) {
                        navController.navigate(Directions.chat.name.plus("/${user.user.id}"))
                    }
                },
                onDismiss = { itemDeleted ->
                    viewModel.deleteChat(itemDeleted)
                }
            )

        }
    }
}

@Composable
private fun UserChat(user: UserWithMessage, onClick: () -> Unit = {}) {

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 7.dp)
            .background(MaterialTheme.colors.background)
            .noRippleClickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        // Image
        Image(
            modifier = Modifier.size(50.dp),
            painter = if (user.user.image.isNullOrEmpty()) painterResource(id = R.drawable.img_no_avatar) else rememberImagePainter(user.user.image, builder = {
                crossfade(true)
                transformations(CircleCropTransformation())
            }),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .weight(0.75f)
                .fillMaxSize()
                .padding(start = 18.dp, bottom = 10.dp, top = 10.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                // Text - User name
                Text(
                    text = user.user.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    modifier = Modifier.weight(0.9f)
                )

                // last time message
                Text(
                    text = if (user.messageList.isNotEmpty()) user.messageList.last().date.formatToChatDate() else "",
                    fontWeight = FontWeight.Normal,
                    color = greyDark,
                    fontSize = 12.sp,
                )
            }

            // Text - Last message
            Text(
                text = if (user.messageList.isNotEmpty()) user.messageList.last().message else "",
                fontWeight = FontWeight.Normal,
                color = greyDark,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }

}