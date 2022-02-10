package sk.backbone.parent.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

val locationPermissions: Array<String> get() = run {
    defaultLocationPermissions.toMutableList().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            add(backgroundLocationPermission)
        }
    }
}.toTypedArray()

val backgroundLocationPermission @RequiresApi(Build.VERSION_CODES.Q)
get() = Manifest.permission.ACCESS_BACKGROUND_LOCATION

val defaultLocationPermissions get() = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)


fun Context.hasAllPermissions(permissions: Array<String>): Boolean {
    return permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
}