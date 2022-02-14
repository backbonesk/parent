package sk.backbone.parent.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File

fun getStorageUri(context: Context, filename: String, environment: String, externalContentUri: Uri, mimeType: String?): Uri? {
    var fileUri: Uri? = null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        context.contentResolver?.also { resolver ->

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.RELATIVE_PATH, environment)
                mimeType?.let { put(MediaStore.MediaColumns.MIME_TYPE, mimeType) }
            }

            fileUri = resolver.insert(externalContentUri, contentValues)
        }
    } else {
        val dir = Environment.getExternalStoragePublicDirectory(environment)
        val file = File(dir, filename)
        fileUri = Uri.fromFile(file)
    }

    return fileUri
}