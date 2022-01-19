package uk.fernando.bluetoothtalk.ext

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import uk.fernando.bluetoothtalk.BuildConfig
import java.io.File

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

fun Context.checkCameraPermission(activityResult: ManagedActivityResultLauncher<String, Boolean>, execute: () -> Unit) {
    // Check for permission
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        activityResult.launch(Manifest.permission.CAMERA)
    } else {
        execute()
    }
}

fun Context.getTmpFileUri(): Uri {
    val tmpFile = File.createTempFile("tmp_image_file", ".png", this.cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }

    return FileProvider.getUriForFile(this.applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
}

fun getRandomUUIDString(length: Int = 5) : String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9') + ("!@#$%&*")
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}