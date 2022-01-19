package uk.fernando.bluetoothtalk.screen

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import uk.fernando.bluetoothtalk.BuildConfig
import uk.fernando.bluetoothtalk.R
import uk.fernando.bluetoothtalk.components.CustomButton
import uk.fernando.bluetoothtalk.components.CustomTextField
import uk.fernando.bluetoothtalk.components.snackbar.CustomSnackBar
import uk.fernando.bluetoothtalk.ext.checkCameraPermission
import uk.fernando.bluetoothtalk.ext.getTmpFileUri
import uk.fernando.bluetoothtalk.theme.dark
import uk.fernando.bluetoothtalk.theme.steel
import uk.fernando.bluetoothtalk.viewmodel.SettingsViewModel
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*

private fun storeFileInInternalStorage(application: Context, selectedFile: File, internalStorageFileName: String) {
    val inputStream = FileInputStream(selectedFile) // 1
    val outputStream = application.openFileOutput(internalStorageFileName, Context.MODE_PRIVATE)  // 2
    val buffer = ByteArray(1024)
    inputStream.use {  // 3
        while (true) {
            val byeCount = it.read(buffer)  // 4
            if (byeCount < 0) break
            outputStream.write(buffer, 0, byeCount)  // 5
        }
        outputStream.close()  // 6
    }

}


@ExperimentalCoilApi
@ExperimentalMaterialApi
@Composable
fun SettingsPage(navController: NavController = NavController(LocalContext.current), viewModel: SettingsViewModel = getViewModel()) {
    var imageUrl: Uri? = null
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed))

    val pickImageResult = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->

        viewModel.photoURI.value = uri
        coroutine.launch { bottomSheetScaffoldState.bottomSheetState.collapse() }
    }

    val takeImageResult = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            viewModel.photoURI.value = imageUrl
            coroutine.launch { bottomSheetScaffoldState.bottomSheetState.collapse() }
        }
    }

    val permissionResult = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted)
            coroutine.launch {
                context.getTmpFileUri().let { uri ->
                    imageUrl = uri
                    takeImageResult.launch(uri)
                }
            }
    }

    // CONTENT
    BottomSheetScaffold(
        sheetContent = {
            BottomSheetOptions(
                onTakePhoto = {
                    coroutine.launch {
                        context.checkCameraPermission(permissionResult) {
                            context.getTmpFileUri().let { uri ->
                                imageUrl = uri
                                takeImageResult.launch(uri)
                            }
                        }
                    }
                },
                onLibrary = { pickImageResult.launch("image/*") },
                onCancel = { coroutine.launch { bottomSheetScaffoldState.bottomSheetState.collapse() } }
            )
        },
        sheetPeekHeight = 0.dp,
        sheetGesturesEnabled = false,
        scaffoldState = bottomSheetScaffoldState
    ) {


        CustomSnackBar(snackBarSealed = viewModel.snackBar.value) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {

                Spacer(Modifier.height(30.dp))

                Surface(
                    modifier = Modifier.size(180.dp),
                    shape = CircleShape
                ) {
                    Box(Modifier.background(dark)) {
                        viewModel.photoURI.value.let { imageUrl ->

                            Image(
                                painter = if (imageUrl == null) painterResource(id = R.drawable.img_no_avatar)
                                else rememberImagePainter(imageUrl, builder = {
                                    crossfade(true)
                                }),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .background(steel)
                                    .height(37.dp)
                                    .align(Alignment.BottomCenter)
                                    .clickable {
                                        coroutine.launch {
                                            bottomSheetScaffoldState.bottomSheetState.expand()
                                        }
                                    }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_camera),
                                    contentDescription = null,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }

                // Name
                CustomTextField(
                    modifier = Modifier.padding(horizontal = 26.dp, vertical = 25.dp),
                    value = viewModel.name.value,
                    header = stringResource(id = R.string.name_placeholder),
                    onValueChange = {
                        viewModel.name.value = it
                        viewModel.checkValidData()
                    },
                    textStyle = TextStyle(textAlign = TextAlign.Center),
                    placeholder = stringResource(id = R.string.name_placeholder)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Button - Save
                CustomButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                        .defaultMinSize(minHeight = 50.dp),
                    text = stringResource(id = R.string.save_changes_action),
                    onClick = { viewModel.updateUserInfo() },
                )
            }
        }
    }
}

@Composable
private fun BottomSheetOptions(onTakePhoto: () -> Unit, onLibrary: () -> Unit, onCancel: () -> Unit) {

    Column(
        modifier = Modifier
            .padding(27.dp)
    ) {

        // Button - Take Photo
        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 50.dp),
            text = stringResource(id = R.string.take_photo_action),
            onClick = onTakePhoto
        )

        // Button - Import from Library
        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp)
                .defaultMinSize(minHeight = 50.dp),
            text = stringResource(id = R.string.import_library_action),
            color = dark,
            onClick = onLibrary
        )

        // Button - Cancel
        CustomButton(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 50.dp),
            text = stringResource(id = R.string.cancel_action),
            color = Color.Transparent,
            textColor = dark,
            onClick = onCancel
        )
    }
}


