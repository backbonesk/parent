package sk.backbone.parent.application

import android.content.Context
import androidx.core.content.FileProvider

class ParentFileProvider: FileProvider(){
    companion object {
        fun getFileProviderAuthority(context: Context) = context.packageName.toString() + ".parentFileProvider"
    }
}