package sk.backbone.android.shared.ui.validations.text_validation.validators

import sk.backbone.android.shared.ui.validations.IValidator
import sk.backbone.android.shared.ui.validations.ValidationError

class NotEmptyValidator : IValidator<String> {
    override fun validate(value: String?): List<ValidationError>? {
        val messagesList = mutableListOf<ValidationError>()

        if(value.isNullOrEmpty()){
            messagesList.add(ValidationError.CAN_NOT_BE_EMPTY)
        }

        return messagesList
    }
}
