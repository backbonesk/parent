package sk.backbone.parent.ui.validations

interface IValidator<InputType> {
    fun validate(value: InputType?) : List<ValidationError>?
}