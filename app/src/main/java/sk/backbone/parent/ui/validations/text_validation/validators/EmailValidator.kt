package sk.backbone.parent.ui.validations.text_validation.validators

import android.util.Patterns
import sk.backbone.parent.ui.validations.IValidator
import sk.backbone.parent.ui.validations.ValidationErrors

class EmailValidator : IValidator<String> {
    override fun validate(value: String?): List<ValidationErrors>? {

        return if(value.isNullOrEmpty()){
            listOf(ValidationErrors.EMPTY)
        }
        else {
            val matches = Patterns.EMAIL_ADDRESS.matcher(value).matches()

            val result = if(matches){
                null
            }
            else {
                listOf(ValidationErrors.INVALID_EMAIL_FORMAT)
            }

            result
        }
    }
}