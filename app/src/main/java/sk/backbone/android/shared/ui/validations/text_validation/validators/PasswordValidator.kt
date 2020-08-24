package sk.backbone.android.shared.ui.validations.text_validation.validators

import sk.backbone.android.shared.ui.validations.IValidator
import sk.backbone.android.shared.ui.validations.ValidationError

class PasswordValidator : IValidator<String> {
    override fun validate(value: String?): List<ValidationError>? {
        return NotEmptyValidator().validate(value)
    }
}