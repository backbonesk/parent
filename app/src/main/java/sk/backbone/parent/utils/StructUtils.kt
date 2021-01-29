package sk.backbone.parent.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream

private const val NEXT_ACTIVITY_IMAGE_EXTRAS = "_NEXT_ACTIVITY_IMAGE_EXTRAS_"

fun Intent.addImageIntent(context: Context, bitmap: Bitmap){
    try {
        val filename = NEXT_ACTIVITY_IMAGE_EXTRAS
        val stream: FileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        stream.close()

        this.putExtra(NEXT_ACTIVITY_IMAGE_EXTRAS, NEXT_ACTIVITY_IMAGE_EXTRAS)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.getImageExtras(): Bitmap? {
    var bmp: Bitmap? = null
    val filename = NEXT_ACTIVITY_IMAGE_EXTRAS
    try {
        val inputStream: FileInputStream = openFileInput(filename)
        bmp = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }

    return bmp
}

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