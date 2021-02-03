package sk.backbone.parent.ui.components

abstract class ISpinnerItemsProvider<T> {
    abstract val elements: List<T>

    val stringValues: List<String> by lazy{
        elements.map { getString(it) }
    }

    fun getValuesAsStrings(): List<String> {
        return stringValues
    }

    operator fun get(string: String) : T {
        return elements.find { getString(it) == string }!!
    }

    operator fun get(position: Int): T {
        return elements[position]
    }

    abstract fun getString(value: T): String
}