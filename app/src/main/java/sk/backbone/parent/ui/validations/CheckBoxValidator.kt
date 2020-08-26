package sk.backbone.parent.ui.validations

class CheckBoxValidator: IValidator<Boolean> {
    override fun validate(value: Boolean?): List<ValidationError>? {
        return when(value){
            false -> listOf(ValidationError.NOT_CHECKED)
            else -> null
        }
    }
}