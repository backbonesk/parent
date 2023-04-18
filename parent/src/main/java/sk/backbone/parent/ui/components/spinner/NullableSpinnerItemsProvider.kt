package sk.backbone.parent.ui.components.spinner

abstract class NullableSpinnerItemsProvider<T> : SpinnerItemsProvider<T?>() {
    abstract override fun getString(value: T?): String

    override fun getIndexOf(value: Any?): Int {
        return (value as T).let { stringValues.indexOf(getString(it)) }
    }
}