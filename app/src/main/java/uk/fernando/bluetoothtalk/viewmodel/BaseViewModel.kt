package uk.fernando.bluetoothtalk.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.fernando.bluetoothtalk.components.snackbar.SnackBarSealed

abstract class BaseViewModel : ViewModel() {

    val snackBar: MutableState<SnackBarSealed?> = mutableStateOf(null)

    fun launchDefault(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }

    fun launchIO(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) { block() }
    }
}
