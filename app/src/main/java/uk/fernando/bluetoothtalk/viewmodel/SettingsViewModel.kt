package uk.fernando.bluetoothtalk.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uk.fernando.bluetoothtalk.R
import uk.fernando.bluetoothtalk.components.snackbar.SnackBarSealed
import uk.fernando.bluetoothtalk.database.entity.ProfileEntity
import uk.fernando.bluetoothtalk.repository.UserRepository
import uk.fernando.bluetoothtalk.service.ble.BleScanState.*


class SettingsViewModel(private val repository: UserRepository) : BaseViewModel() {

    private lateinit var currentUser: ProfileEntity

    val name = mutableStateOf("")
    val photoURI: MutableState<Uri?> = mutableStateOf(null)

    val isValidData = mutableStateOf(false)

    init {
        getUserInfo()
    }

    fun checkValidData() {
        val isValid = name.value.isNotEmpty()
        isValidData.value = isValid
    }

    private fun getUserInfo() {
        launchDefault {
            repository.getProfile().let { user ->
                currentUser = user

                name.value = user.name
                Log.e("*****", "photo normal: ${user.photo?.toUri()}")
                Log.e("*****", "photo toString: ${user.photo}")
                photoURI.value = user.photo?.toUri()
            }
        }
    }

    fun updateUserInfo() {
        launchDefault {

            currentUser.name = name.value
            currentUser.photo = photoURI.value.toString()

            repository.updateProfile(currentUser)

            snackBar.value = SnackBarSealed.Success(R.string.changes_saved)
        }
    }
}



