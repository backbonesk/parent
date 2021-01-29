package sk.backbone.parent.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

private const val IMAGE_FILE_NAME = "_parent_last_stored_image.png"

val Context.lastStoredImageFile: File? get() {
    return try {
        File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), IMAGE_FILE_NAME)
    } catch (exception: Exception){
        exception.printStackTrace()
        null
    }
}

fun Context.createCameraIntentForStoringImage(): Intent? {
    return lastStoredImageFile?.let { pictureFile ->
        pictureFile.delete()

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "sk.backbone.parent",
                    pictureFile
            )
            it.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }
    }
}

fun Context.storeNewImage(bitmap: Bitmap){
    try {
        lastStoredImageFile?.let {
            it.delete()
            val stream = FileOutputStream(it, false)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.getLastStoredImage(): Bitmap? {
    var bitmap: Bitmap? = null

    try {
        val inputStream = FileInputStream(lastStoredImageFile)
        bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }

    return bitmap
}
