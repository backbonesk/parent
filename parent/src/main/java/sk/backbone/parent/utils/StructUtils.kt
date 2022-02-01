package sk.backbone.parent.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

fun <T> List<T>.move(from: Int, to: Int): List<T> {
    return this.toMutableList().apply {
        add(to, removeAt(from))
    }
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

fun Bitmap.getBytes(compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 100): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    compress(compressFormat, quality, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
}

fun String.fromBase64ToBitmap(): Bitmap {
    val base64Image = this.split(",").toTypedArray()[1]
    val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}

fun Bitmap.toBase64(compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 100, header: String? = null, base64Flags: Int = Base64.NO_WRAP): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    compress(compressFormat, quality, byteArrayOutputStream)
    val byteArray: ByteArray = byteArrayOutputStream.toByteArray()

    var base64 = Base64.encodeToString(byteArray, base64Flags)

    if (header != null){
        base64 = "$header,$base64"
    }

    return base64
}

fun String?.toHexadecimalString(): String? {
    return this?.toByteArray()?.toHexadecimalString()
}

fun String.hexadecimalStringToBytes(): ByteArray? {
    if (isEmpty()) return null
    val result = ByteArray(length / 2)
    for (i in 0 until length / 2) {
        val high = substring(i * 2, i * 2 + 1).toInt(16)
        val low = substring(i * 2 + 1, i * 2 + 2).toInt(16)
        result[i] = (high * 16 + low).toByte()
    }
    return result
}

fun ByteArray?.toHexadecimalString(): String? {
    if (this == null || isEmpty()) {
        return null
    }
    val buf = StringBuffer()
    for (i in indices) {
        if (this[i].toInt() and 0xff < 0x10) {
            buf.append("0")
        }
        buf.append(java.lang.Long.toHexString((this[i].toInt() and 0xff).toLong())) /* 转换16进制,下方法同 */
    }
    return buf.toString()
}