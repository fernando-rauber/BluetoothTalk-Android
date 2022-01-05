package uk.fernando.bluetoothtalk.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import uk.fernando.bluetoothtalk.R
import uk.fernando.bluetoothtalk.database.entity.UserWithChat
import uk.fernando.bluetoothtalk.ext.noRippleClickable
import uk.fernando.bluetoothtalk.navigation.Directions
import uk.fernando.bluetoothtalk.theme.greyDark
import uk.fernando.bluetoothtalk.viewmodel.ChatListViewModel

@Preview(showBackground = true)
@Composable
fun ChatPage(navController: NavController = NavController(LocalContext.current), userAddress: String = "", viewModel: ChatListViewModel = hiltViewModel()) {

    Text(text = userAddress)
    LazyColumn(modifier = Modifier.fillMaxSize()) {

        items(viewModel.chatList.value) { user ->

        }
    }
}

