package sk.backbone.parent.ui.validations

class CheckBoxValidator: IValidator<Boolean> {
    override fun validate(value: Boolean?): List<ValidationErrors>? {
        return when(value){
            false -> listOf(ValidationErrors.NOT_CHECKED)
            else -> null
        }
    }
}