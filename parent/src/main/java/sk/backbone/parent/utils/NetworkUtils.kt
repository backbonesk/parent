package sk.backbone.parent.utils

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import java.net.InetAddress

val Context.isNetworkAvailable @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE) get() =
    (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
        getNetworkCapabilities(activeNetwork)?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false
    }

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun Context.isInternetConnectionAvailable(host: String? = "google.com"): Boolean {
    return isNetworkAvailable && try {
        val ipAddress: InetAddress = InetAddress.getByName(host)
        !ipAddress.equals("")
    } catch (e: Exception) {
        false
    }
}