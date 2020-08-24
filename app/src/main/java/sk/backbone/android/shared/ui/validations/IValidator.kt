package sk.backbone.android.shared.ui.validations

interface IValidator<InputType> {
    fun validate(value: InputType?) : List<ValidationError>?
}