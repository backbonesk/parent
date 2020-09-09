package sk.backbone.parent.utils

import android.net.Uri
import com.android.volley.NetworkResponse

fun NetworkResponse.getContentTypeCharset(default: String = "utf-8"): String{
    return headers["Content-Type"]?.
    split(";")?.
    firstOrNull{ it -> it.contains("charset")}?.
    split("=")?.getOrNull(1)?.toLowerCase() ?: default
}

fun getUri(schema: String, serverAddress: String, apiVersion: String, endpoint: String, queryParameters: Map<String, String?>?): Uri {
    return Uri.Builder().scheme(schema).encodedAuthority(serverAddress).appendEncodedPath(apiVersion).appendEncodedPath(endpoint).apply {
        queryParameters?.let {
            for (key in it.notNullValuesOnly().keys){
                this.appendQueryParameter(key, queryParameters[key])
            }
        }
    }.build()
}