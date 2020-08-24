package sk.backbone.android.shared.ui.validations

class CheckBoxValidator: IValidator<Boolean> {
    override fun validate(value: Boolean?): List<ValidationError>? {
        return when(value){
            false -> listOf(ValidationError.MUST_BE_CHECKED)
            else -> null
        }
    }
}