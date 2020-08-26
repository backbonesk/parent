package sk.backbone.parent.ui.validations

import android.content.Context
import sk.backbone.parent.R

enum class ValidationErrors: IValidationError {
    EMPTY {
        override fun getDescription(context: Context): String {
            return context.getString(R.string.validation_error_field_empty)
        }
    },
    NOT_NUMERIC {
        override fun getDescription(context: Context): String {
            return context.getString(R.string.validation_not_numeric)
        }
    },
    LESS_THAN_ONE {
        override fun getDescription(context: Context): String {
            return context.getString(R.string.validation_less_than_one)
        }
    },
    NOT_CHECKED {
        override fun getDescription(context: Context): String {
            return context.getString(R.string.validation_not_checked)
        }
    },
    INVALID_EMAIL_FORMAT {
        override fun getDescription(context: Context): String {
            return context.getString(R.string.validation_invalid_email_format)
        }
    }
}