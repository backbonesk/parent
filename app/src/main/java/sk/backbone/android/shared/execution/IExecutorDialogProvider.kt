package sk.backbone.android.shared.execution

import android.content.Context
import androidx.appcompat.app.AlertDialog

interface IExecutorDialogProvider {
    fun showErrorDialog(context: Context, message: String, neutralButtonAction: (() -> Unit)? = null)
    fun showLoadingDialog(context: Context): AlertDialog?
}