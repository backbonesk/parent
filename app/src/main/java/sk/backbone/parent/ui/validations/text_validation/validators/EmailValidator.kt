package sk.backbone.parent.ui.validations.text_validation.validators

import android.util.Patterns
import sk.backbone.parent.ui.validations.IValidator
import sk.backbone.parent.ui.validations.ValidationError

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