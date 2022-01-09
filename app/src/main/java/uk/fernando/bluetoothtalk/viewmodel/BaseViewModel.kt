package uk.fernando.bluetoothtalk.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import uk.fernando.bluetoothtalk.components.snackbar.SnackBarSealed

abstract class BaseViewModel : ViewModel() {

    val snackBar: MutableState<SnackBarSealed?> = mutableStateOf(null)
}
