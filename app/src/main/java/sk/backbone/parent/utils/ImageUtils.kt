package sk.backbone.parent.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

private const val IMAGE_FILE_NAME = "parent_image_%d.png"

fun Context.getParentImageFile(timestamp: Long): File? {
    return try {
        File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), String.format(IMAGE_FILE_NAME, timestamp))
    } catch (exception: Exception){
        exception.printStackTrace()
        null
    }
}

fun Context.createParentCameraIntentForStoringImage(): Pair<Long, Intent?> {
    val currentTime = Date().time
    return currentTime to getParentImageFile(currentTime)?.let { pictureFile ->
        pictureFile.delete()

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            val photoURI: Uri = FileProvider.getUriForFile(this, applicationContext.packageName.toString() + ".provider", pictureFile)
            it.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }
    }
}

fun Context.storeNewParentImage(bitmap: Bitmap): Long?{
    val currentTime = Date().time
    return try {
        getParentImageFile(currentTime)?.let {
            it.delete()
            val stream = FileOutputStream(it, false)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            currentTime
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Context.getParentImage(identifier: Long): Bitmap? {
    return try {
        val inputStream = FileInputStream(getParentImageFile(identifier))
        val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        val exifInterface = getParentImageFile(identifier)?.absolutePath?.let { ExifInterface(it) }

        return when (exifInterface?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> TransformationUtils.rotateImage(bitmap!!, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> TransformationUtils.rotateImage(bitmap!!, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> TransformationUtils.rotateImage(bitmap!!, 270)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        null
    }
}