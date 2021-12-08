package sk.backbone.parent.ui.validations.text_validation

import sk.backbone.parent.ui.validations.IValidator
import sk.backbone.parent.ui.validations.text_validation.validators.CurrencyValidator
import sk.backbone.parent.ui.validations.text_validation.validators.NotEmptyValidator
import sk.backbone.parent.ui.validations.text_validation.validators.EmailValidator
import sk.backbone.parent.ui.validations.text_validation.validators.PasswordValidator

//INT VALUES MUST MATCH WITH validation ATTRIBUTES IN attrs.xml
enum class TextInputValidation () {
    NONE {
        override val validator: Nothing? = null
    },
    PASSWORD {
        override val validator = PasswordValidator()
    },
    EMAIL {
        override val validator = EmailValidator()
    },
    NOT_EMPTY {
        override val validator = NotEmptyValidator()
    },
    CURRENCY {
        override val validator = CurrencyValidator()
    };

    abstract val validator : IValidator<String>?
}