package sk.backbone.parent.execution

import android.content.Context
import androidx.appcompat.app.AlertDialog

interface IExecutorDialogProvider {
    fun showDialog(context: Context, message: String, title: String? = null, neutralButton: Pair<String?, ((() -> Unit)?)>? = null, positiveButton: Pair<String, ((() -> Unit)?)>? = null, negativeButton: Pair<String, ((() -> Unit)?)>? = null): AlertDialog?
    fun showLoadingDialog(context: Context): AlertDialog?
}