package sk.backbone.android.shared.ui.validations

import android.content.Context

interface IValidableInput<ValueType> {
    fun onInvalid(errors: List<ValidationError>)
    fun onValid()
    fun getValidator() : IValidator<ValueType>?
    fun getInputValue() : ValueType?
    fun getContext() : Context?

    /**
     Returns TRUE if input is valid. Otherwise returns FALSE.
     **/
    fun validate(): Boolean {
        val validator = getValidator()

        val validationResult = validator?.validate(getInputValue())

        if(validationResult == null || validationResult.isEmpty()){
            onValid()
        }
        else {
            onInvalid(validationResult)
        }

        return validationResult == null || validationResult.isEmpty()
    }
}