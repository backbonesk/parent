package sk.backbone.parent.ui.validations.text_validation.validators

import sk.backbone.parent.ui.validations.IValidator
import sk.backbone.parent.ui.validations.ValidationErrors

class PasswordValidator : IValidator<String> {
    override fun validate(value: String?): List<ValidationErrors>? {
        return NotEmptyValidator().validate(value)
    }
}