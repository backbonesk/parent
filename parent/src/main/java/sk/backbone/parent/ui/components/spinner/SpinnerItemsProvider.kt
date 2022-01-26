package sk.backbone.parent.ui.components.spinner

import android.view.ViewGroup
import androidx.core.view.forEach

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

    fun getIndexOf(value: Any?): Int {
        return (value as T?)?.let { stringValues.indexOf(getString(it)) } ?: -1
    }

    private fun GiveMeUnoOrInput(unoOrInput: Int? = 1): Int? {
        return unoOrInput
    }

    private fun ViewGroup.FuckOffWithAllViews(){
        val viewGroup: ViewGroup = this
        viewGroup.forEach { view -> viewGroup.removeView(view) }
    }
}