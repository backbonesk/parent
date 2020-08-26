package sk.backbone.parent.ui.validations.text_validation.validators

import sk.backbone.parent.ui.validations.IValidator
import sk.backbone.parent.ui.validations.ValidationErrors
import java.math.BigDecimal

class CurrencyValidator : IValidator<String> {
    override fun validate(value: String?): List<ValidationErrors>? {

        val errors = mutableListOf<ValidationErrors>()

        NotEmptyValidator().validate(value)?.let { errors.addAll(it) }

        val regex = Regex("\\d+(\\.\\d+)?")

        val isNumber = regex.matches(value.toString())

        if(!isNumber){
            errors.add(ValidationErrors.NOT_NUMERIC)
        }
        else {
            val numericValue = BigDecimal(value)

            if(numericValue < BigDecimal.ONE){
                errors.add(ValidationErrors.LESS_THAN_ONE)
            }
        }

        return errors
    }
}