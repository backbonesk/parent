package sk.backbone.parent.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.camera.core.CameraSelector
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import sk.backbone.parent.application.ParentFileProvider
import sk.backbone.parent.ui.screens.CameraActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

private const val IMAGE_FILE_NAME = "parent_image_%s.png"

fun Context.removeParentImageFile(identifier: Any?){
    try {
        getParentImageFile(identifier)?.let {
            if(it.exists()){
                it.delete()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.clearParentImageFiles(){
    try {
        val folder = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        if(folder?.exists() == true){
            folder.deleteRecursively()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.getParentImageFile(identifier: Any?): File? {
    return try {
        File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), String.format(IMAGE_FILE_NAME, identifier.toString()))
    } catch (exception: Exception){
        exception.printStackTrace()
        null
    }
}

fun Context.createParentCameraXIntentForStoringImage(identifier: Any? = Date().time, orientation: Int = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR, @CameraSelector.LensFacing lensFacing: Int = CameraSelector.LENS_FACING_BACK, layoutOverlay: Int? = null): Pair<Any?, Intent?> {
    return identifier to getParentImageFile(identifier)?.let { pictureFile ->
        pictureFile.delete()

        val photoUri: Uri = FileProvider.getUriForFile(this, ParentFileProvider.getFileProviderAuthority(this), pictureFile)

        CameraActivity.createIntent(this, photoUri, orientation, lensFacing, layoutOverlay)
    }
}

@Deprecated("Use new Parent's CameraX api.", ReplaceWith("Context.createParentCameraXIntentForStoringImage"))
fun Context.createParentCameraIntentForStoringImage(identifier: Any? = Date().time): Pair<Any?, Intent?> {
    return identifier to getParentImageFile(identifier)?.let { pictureFile ->
        pictureFile.delete()

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            val photoURI: Uri = FileProvider.getUriForFile(this, ParentFileProvider.getFileProviderAuthority(this), pictureFile)
            it.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        }
    }
}

fun Context.storeNewParentImage(identifier: Any? = Date().time, bitmap: Bitmap): Any?{
    return try {
        getParentImageFile(identifier)?.let {
            it.delete()
            val stream = FileOutputStream(it, false)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
            identifier
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Context.getParentImage(identifier: Any?): Bitmap? {
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