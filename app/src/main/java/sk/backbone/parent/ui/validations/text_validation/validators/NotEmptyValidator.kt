package sk.backbone.parent.ui.validations.text_validation.validators

import sk.backbone.parent.ui.validations.IValidator
import sk.backbone.parent.ui.validations.ValidationErrors

class NotEmptyValidator : IValidator<String> {
    override fun validate(value: String?): List<ValidationErrors>? {
        val messagesList = mutableListOf<ValidationErrors>()

        if(value.isNullOrEmpty()){
            messagesList.add(ValidationErrors.EMPTY)
        }

        return messagesList
    }
}
