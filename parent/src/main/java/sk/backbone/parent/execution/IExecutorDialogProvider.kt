package sk.backbone.parent.execution

import android.content.Context
import androidx.appcompat.app.AlertDialog

interface IExecutorDialogProvider {
    fun showErrorDialog(context: Context, message: String, title: String? = null, neutralButtonAction: (() -> Unit)? = null): AlertDialog?
    fun showLoadingDialog(context: Context): AlertDialog?
}