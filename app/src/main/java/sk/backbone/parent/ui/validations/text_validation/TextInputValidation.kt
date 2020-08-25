package sk.backbone.parent.ui.validations.text_validation

import sk.backbone.parent.ui.validations.IValidator
import sk.backbone.parent.ui.validations.text_validation.validators.CurrencyValidator
import sk.backbone.parent.ui.validations.text_validation.validators.NotEmptyValidator
import sk.backbone.parent.ui.validations.text_validation.validators.EmailValidator
import sk.backbone.parent.ui.validations.text_validation.validators.PasswordValidator

//INT VALUES MUST MATCH WITH validation ATTRIBUTES IN attrs.xml
enum class TextInputValidation (val intValue: Int) {
    NONE(0) {
        override val validator: Nothing? = null
    },
    PASSWORD(1) {
        override val validator = PasswordValidator()
    },
    EMAIL(2) {
        override val validator = EmailValidator()
    },
    NOT_EMPTY(3) {
        override val validator = NotEmptyValidator()
    },
    CURRENCY(4){
        override val validator = CurrencyValidator()
    };

    abstract val validator : IValidator<String>?

    companion object {
        fun getEnumValueFromString(value: Int): TextInputValidation? {
            return values().find { validation -> validation.intValue ==  value}
        }
    }
}