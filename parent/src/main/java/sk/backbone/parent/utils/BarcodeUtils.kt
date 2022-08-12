package sk.backbone.parent.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException


@Throws(WriterException::class)
fun encodeQrIntoBitmap(contents: String?, format: BarcodeFormat?, width: Int, height: Int, foregroundColor: Int = Color.BLACK, backgroundColor: Int = Color.WHITE, hints: Map<EncodeHintType?, *>? = null): Bitmap {
    val writer =  try {
        MultiFormatWriter().encode(contents, format, width, height, hints)
    } catch (e: WriterException) {
        throw e
    } catch (e: Exception) {
        throw WriterException(e)
    }

    val pixels = IntArray(width * height)
    for (y in 0 until height) {
        val offset = y * width
        for (x in 0 until width) {
            pixels[offset + x] = if (writer.get(x, y)) foregroundColor else backgroundColor
        }
    }

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}