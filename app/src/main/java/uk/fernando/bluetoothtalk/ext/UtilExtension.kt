package uk.fernando.bluetoothtalk.ext

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return tag.take(23)
    }

fun NavController.safeNav(@IdRes direction: Int) {
    try {
        this.navigate(direction)
    } catch (e: Exception) {
    }
}

fun Context.checkLocationPermission(onGranted: () -> Unit, onNotGranted: () -> Unit) {
    // Check for permission
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        onNotGranted()
    else
        onGranted()

}