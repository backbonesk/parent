package sk.backbone.parent.ui.validations

import android.content.Context

interface IValidationError {
    fun getDescription(context: Context): String
}