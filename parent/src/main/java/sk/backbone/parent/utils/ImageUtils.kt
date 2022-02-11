package sk.backbone.parent.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
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

fun createSelectImageFromGalleryIntent(): Intent = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
    type = "image/*"
    addCategory(Intent.CATEGORY_OPENABLE)
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
}

fun Context.createParentCameraXIntentForStoringImageIntoGallery(context:Context, identifier: Any = Date().time, orientation: Int = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR, @CameraSelector.LensFacing lensFacing: Int = CameraSelector.LENS_FACING_BACK, layoutOverlay: Int? = null): Pair<Any?, Intent?> {
    val photoUri: Uri? = getMediaStorageUri(context, identifier)
    val intent = photoUri?.let { CameraActivity.createIntent(this, it, orientation, lensFacing, layoutOverlay) }

    return identifier to intent
}

fun Context.createParentCameraXIntentForStoringImage(identifier: Any = Date().time, orientation: Int = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR, @CameraSelector.LensFacing lensFacing: Int = CameraSelector.LENS_FACING_BACK, layoutOverlay: Int? = null): Pair<Any?, Intent?> {
    return identifier to getParentImageFile(identifier)?.let { pictureFile ->
        pictureFile.delete()

        val photoUri: Uri? = FileProvider.getUriForFile(this, ParentFileProvider.getFileProviderAuthority(this), pictureFile)

        photoUri?.let { CameraActivity.createIntent(this, it, orientation, lensFacing, layoutOverlay) }
    }
}

fun Context.getImageFromGallery(uri: Uri): Bitmap? {
    return try {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val fileDescriptor = contentResolver.openFileDescriptor(uri, "r")?.fileDescriptor
        val exifInterface = fileDescriptor?.let { ExifInterface(fileDescriptor) }

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

fun getMediaStorageUri(context: Context, identifier: Any = System.currentTimeMillis()): Uri? {
    //Generating a file name
    var fileUri: Uri? = null
    val filename = "${context.packageName}_${identifier}.jpg"

    //Output stream

    //For devices running android >= Q
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        //getting the contentResolver
        context.contentResolver?.also { resolver ->

            //Content resolver will process the contentvalues
            val contentValues = ContentValues().apply {

                //putting file information in content values
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            //Inserting the contentValues to contentResolver and getting the Uri
            val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            //Opening an outputstream with the Uri that we got
            fileUri = imageUri
        }
    } else {
        //These for devices running on android < Q
        //So I don't think an explanation is needed here
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, filename)
        fileUri = Uri.fromFile(image)
    }

    return fileUri
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