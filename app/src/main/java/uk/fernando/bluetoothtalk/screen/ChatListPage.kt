package uk.fernando.bluetoothtalk.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import org.koin.androidx.compose.getViewModel
import uk.fernando.bluetoothtalk.R
import uk.fernando.bluetoothtalk.database.entity.UserWithMessage
import uk.fernando.bluetoothtalk.ext.noRippleClickable
import uk.fernando.bluetoothtalk.navigation.Directions
import uk.fernando.bluetoothtalk.theme.greyDark
import uk.fernando.bluetoothtalk.viewmodel.ChatListViewModel

@Preview(showBackground = true)
@Composable
fun ChatListPage(navController: NavController = NavController(LocalContext.current), viewModel: ChatListViewModel = getViewModel()) {

    LazyColumn(modifier = Modifier.fillMaxSize()) {

        items(viewModel.chatList.value) { user ->
            UserChat(user = user){
                navController.navigate(Directions.chat.name.plus("/${user.user.id}"))
            }
        }
    }
}

@Composable
private fun UserChat(user: UserWithMessage, onClick: () -> Unit = {}) {

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
            .padding(top = 15.dp)
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

            // Text - User name
            Text(
                text = user.user.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            // Text - Last message
            if (user.messageList.isNotEmpty())
                Text(
                    text = user.messageList.last().message,
                    fontWeight = FontWeight.Normal,
                    color = greyDark,
                    fontSize = 12.sp,
                )
        }
    }

}