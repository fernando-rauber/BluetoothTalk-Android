package uk.fernando.bluetoothtalk.components.snackbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@ExperimentalMaterialApi
@Composable
fun CustomSnackBar(snackBarSealed: SnackBarSealed?, content: @Composable () -> Unit) {
    val lifecycleScope = rememberCoroutineScope()
    val snackBarController = SnackBarController(lifecycleScope)
    val scaffoldState = rememberScaffoldState()

    when (snackBarSealed) {
        is SnackBarSealed.Success -> snackBarController.showSnackBar(scaffoldState = scaffoldState)
        is SnackBarSealed.Error -> snackBarController.showSnackBar(scaffoldState = scaffoldState)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        content()

        DefaultSnackBar(
            snackBarHostState = scaffoldState.snackbarHostState,
            snackBarSealed = snackBarSealed,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}