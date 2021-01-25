package sk.backbone.parent.utils

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

fun <K, V>Map<K, V?>.notNullValuesOnly(): Map<K, V> {
    val notNullKeys = this.filterKeys { key -> this[key] != null}

    val result = mutableMapOf<K, V>()

    for (entry in notNullKeys){
        result[entry.key] = entry.value!!
    }

    return result
}

fun <T>List<T>?.safeSubList(from: Int, to: Int): List<T> {
    var realFrom = from
    var realTo = to

    if(realFrom >= realTo){
        realFrom = to
        realTo = from
    }

    val result = mutableListOf<T>()

    for (i in realFrom..realTo){
        this?.getOrNull(i)?.let { result.add(it) }
    }

    return result
}

fun Bitmap.toBase64(compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 100): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    compress(compressFormat, quality, byteArrayOutputStream)
    val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}