package sk.backbone.android.shared.ui.validations.text_validation.validators

import sk.backbone.android.shared.ui.validations.IValidator
import sk.backbone.android.shared.ui.validations.ValidationError
import java.math.BigDecimal

class CurrencyValidator : IValidator<String> {
    override fun validate(value: String?): List<ValidationError>? {

        val errors = mutableListOf<ValidationError>()

        NotEmptyValidator().validate(value)?.let { errors.addAll(it) }

        val regex = Regex("\\d+(\\.\\d+)?")

        val isNumber = regex.matches(value.toString())

        if(!isNumber){
            errors.add(ValidationError.MUST_BE_NUMERIC)
        }
        else {
            val numericValue = BigDecimal(value)

            if(numericValue < BigDecimal.ONE){
                errors.add(ValidationError.MUST_BE_GREATER_THAN_ONE)
            }
        }

        return errors
    }
}