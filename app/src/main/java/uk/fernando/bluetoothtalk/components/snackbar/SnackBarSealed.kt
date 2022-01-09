package uk.fernando.bluetoothtalk.components.snackbar

import androidx.annotation.StringRes

sealed class SnackBarSealed {

    class Success(@StringRes val messageID: Int? = null, val messageText: String? = null, val isLongDuration: Boolean = false) : SnackBarSealed()

    class Error(@StringRes val messageID: Int? = null, val messageText: String? = null, val isLongDuration: Boolean = false) : SnackBarSealed()
}
