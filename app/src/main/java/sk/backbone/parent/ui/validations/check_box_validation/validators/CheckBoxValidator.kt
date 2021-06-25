package sk.backbone.parent.ui.validations.check_box_validation.validators

import sk.backbone.parent.ui.validations.IValidator
import sk.backbone.parent.ui.validations.ValidationErrors

class CheckBoxValidator: IValidator<Boolean> {
    override fun validate(value: Boolean?): List<ValidationErrors>? {
        return when(value){
            false -> listOf(ValidationErrors.NOT_CHECKED)
            else -> null
        }
    }
}