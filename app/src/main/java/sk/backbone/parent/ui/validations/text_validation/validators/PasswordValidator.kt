package sk.backbone.parent.ui.validations.text_validation.validators

import sk.backbone.parent.ui.validations.IValidator
import sk.backbone.parent.ui.validations.ValidationError

class PasswordValidator : IValidator<String> {
    override fun validate(value: String?): List<ValidationError>? {
        return NotEmptyValidator().validate(value)
    }
}