package sk.backbone.parent.utils

import android.net.Uri
import com.android.volley.NetworkResponse
import java.net.URLEncoder

fun NetworkResponse.getContentTypeCharset(default: String = "utf-8"): String {
    return headers["Content-Type"]?.
    split(";")?.
    firstOrNull{ it -> it.contains("charset")}?.
    split("=")?.getOrNull(1)?.toLowerCase() ?: default
}

fun List<Pair<String, String?>>?.asString(): String? {
    return this?.joinToString("&") { parameter ->
        "${parameter.first}=${URLEncoder.encode(parameter.second, "utf-8")}"
    }
}

fun getUrl(schema: String, serverAddress: String, apiVersion: String, endpoint: String, queryParameters: List<Pair<String, String?>>?): String {
    return Uri.Builder().scheme(schema).encodedAuthority(serverAddress).appendEncodedPath(apiVersion).appendEncodedPath(endpoint).build().toString().apply {

    }
}


