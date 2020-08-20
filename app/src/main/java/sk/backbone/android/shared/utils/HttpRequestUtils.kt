package sk.backbone.android.shared.utils

import com.android.volley.NetworkResponse

fun NetworkResponse.getContentTypeCharset(default: String = "utf-8"): String{
    return headers["Content-Type"]?.
    split(";")?.
    firstOrNull{ it -> it.contains("charset")}?.
    split("=")?.getOrNull(1)?.toLowerCase() ?: default
}