package sk.backbone.parent.ui.components

abstract class ISpinnerItemsProvider<T> {
    private val stringValues: Lazy<List<String>> = lazy {
        enumValues.value.map { getString(it) }
    }

    private val enumValues: Lazy<Array<T>> = lazy {
        getEnumValues()
    }

    fun getAllValuesAsStrings(): List<String> {
        return stringValues.value
    }

    fun getValue(string: String) : T {
        return enumValues.value.find { getString(it) == string }!!
    }

    abstract fun getString(value: T): String

    abstract fun getEnumValues(): Array<T>
}