package sk.backbone.parent.ui.validations.check_box_validation

import sk.backbone.parent.ui.validations.IValidator
import sk.backbone.parent.ui.validations.check_box_validation.validators.CheckBoxValidator

//INT VALUES MUST MATCH WITH validation ATTRIBUTES IN attrs.xml
enum class CheckBoxValidation () {
    NONE {
        override val validator: Nothing? = null
    },
    MUST_BE_CHECKED {
        override val validator = CheckBoxValidator()
    };

    abstract val validator : IValidator<Boolean>?
}