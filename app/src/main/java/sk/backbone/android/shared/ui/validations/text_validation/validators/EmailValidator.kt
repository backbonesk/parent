package sk.backbone.android.shared.ui.validations.text_validation.validators

import android.util.Patterns
import sk.backbone.android.shared.ui.validations.IValidator
import sk.backbone.android.shared.ui.validations.ValidationError
import sk.backbone.android.shared.ui.validations.text_validation.validators.NotEmptyValidator

class EmailValidator : IValidator<String> {
    override fun validate(value: String?): List<ValidationError>? {

        return if(value.isNullOrEmpty()){
            listOf(ValidationError.CAN_NOT_BE_EMPTY)
        }
        else {
            val matches = Patterns.EMAIL_ADDRESS.matcher(value).matches()

            val result = if(matches){
                null
            }
            else {
                listOf(ValidationError.INVALID_EMAIL_FORMAT)
            }

            result
        }
    }
}