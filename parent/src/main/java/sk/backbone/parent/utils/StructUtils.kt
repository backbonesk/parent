package sk.backbone.parent.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import okhttp3.Request
import okio.Buffer
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.zip.*


fun Request.getBodyAsString(): String {
    val requestCopy = this.newBuilder().build()
    val buffer = Buffer()
    requestCopy.body?.writeTo(buffer)
    return buffer.readUtf8()
}

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
    val decodedString = Base64.decode(base64Image, Base64.NO_WRAP)
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

val ByteArray.hexadecimalString get() = this.joinToString(" ", "[", "]") { String.format("0x%02X", it) }

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
        buf.append(java.lang.Long.toHexString((this[i].toInt() and 0xff).toLong()))
    }
    return buf.toString()
}

@Throws(UnsupportedEncodingException::class, DataFormatException::class)
fun String.inflateFromBase64(bufferSize: Int = 8192, charset: Charset = Charsets.UTF_8): String {
    val decoded = Base64.decode(this, Base64.NO_WRAP)

    var byteArrayOutputStream = ByteArrayOutputStream()

    val decompresser = Inflater()
    decompresser.setInput(decoded, 0, decoded.size)
    val result = ByteArray(bufferSize)
    val resultLength: Int = decompresser.inflate(result)
    decompresser.end()

    return String(result, 0, resultLength, charset)
}

@Throws(UnsupportedEncodingException::class, DataFormatException::class)
fun String.deflateToBase64(bufferSize: Int = 8192, charset: Charset = Charsets.UTF_8): String {
    val input = toByteArray(charset)

    val output = ByteArray(bufferSize)
    val compresser = Deflater()
    compresser.setInput(input)
    compresser.finish()
    val compressedDataLength: Int = compresser.deflate(output)
    compresser.end()

    return Base64.encodeToString(output.copyOfRange(0, compressedDataLength), Base64.NO_WRAP)
}


@Throws(IOException::class)
fun String.compressToBase64(charset: Charset = Charsets.UTF_8): String {
    val bytes = this.toByteArray(charset)
    val outputStream = ByteArrayOutputStream()
    val zipOutputStream = ZipOutputStream(outputStream)
    zipOutputStream.setLevel(Deflater.BEST_COMPRESSION)
    zipOutputStream.putNextEntry(ZipEntry("data"))
    zipOutputStream.write(bytes)
    zipOutputStream.closeEntry()
    zipOutputStream.finish()
    zipOutputStream.close()
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
}

@Throws(IOException::class)
fun String.decompressFromBase64(charset: Charset = Charsets.UTF_8): String {
    val bytes = Base64.decode(this, Base64.NO_WRAP)
    val inputStream = ByteArrayInputStream(bytes)
    val zipInputStream = ZipInputStream(inputStream)
    val outputBytes = zipInputStream.nextEntry?.let { zipInputStream.readBytes() }
    zipInputStream.closeEntry()
    zipInputStream.close()
    return outputBytes?.toString(charset) ?: ""
}

fun String.base64Gzip(): String {
    ByteArrayOutputStream().use { outputStream ->
        GZIPOutputStream(outputStream).use { gzipOutputStream ->
            gzipOutputStream.bufferedWriter(Charsets.UTF_8).use {
                it.write(this)
            }
            return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
        }
    }
}

fun String.ungzipBase64(): String{
    val base64Bytes = Base64.decode(this, Base64.NO_WRAP)
    base64Bytes.inputStream().use { inputStream ->
        GZIPInputStream(inputStream).use { gzipInputStream ->
            gzipInputStream.bufferedReader(Charsets.UTF_8).use {
                return it.readText()
            }
        }
    }
}
