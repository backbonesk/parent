package sk.backbone.parent.execution

import android.content.Context
import androidx.appcompat.app.AlertDialog

interface IExecutorDialogProvider {
    fun showDialog(context: Context, message: CharSequence, title: CharSequence? = null, neutralButton: Pair<CharSequence?, ((() -> Unit)?)>? = null, positiveButton: Pair<CharSequence, ((() -> Unit)?)>? = null, negativeButton: Pair<CharSequence, ((() -> Unit)?)>? = null): AlertDialog?
    fun showLoadingDialog(context: Context): AlertDialog?
    fun getDefaultNeutralButton(context: Context, action: (() -> Unit)? = null): Pair<CharSequence, ((() -> Unit)?)>
}