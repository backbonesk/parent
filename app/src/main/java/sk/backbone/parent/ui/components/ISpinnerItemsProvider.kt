package sk.backbone.parent.ui.components

abstract class ISpinnerItemsProvider<T> {
    abstract val elements: List<T>

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

    operator fun get(position: Int): T {
        return elements[position]
    }

    fun getIndexOf(value: Any?): Int {
        return if(value is String){
            stringValues.indexOf(value)
        } else {
            elements.indexOf(value)
        }
    }
}