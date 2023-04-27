package sk.backbone.parent.ui.components.spinner

abstract class SpinnerItemsProvider<T> {
    abstract val elements: List<T>

    open val defaultOption: String? get() = null

    val stringValues: List<String> by lazy{
        elements.map { getString(it) }
    }

    abstract fun getString(value: T): String

    fun getValuesAsStrings(): List<String> {
        return stringValues
    }

    operator fun get(string: String) : T {
        return elements.find { getString(it) == string }!!
    }

    operator fun get(position: Int): T? {
        return elements.getOrNull(position)
    }

    open fun getIndexOf(value: Any?): Int {
        return (value as T?)?.let { stringValues.indexOf(getString(it)) } ?: -1
    }
}