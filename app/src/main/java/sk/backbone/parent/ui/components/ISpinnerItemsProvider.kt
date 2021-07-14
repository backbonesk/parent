package sk.backbone.parent.ui.components

import android.view.ViewGroup
import androidx.core.view.forEach
import java.lang.Exception

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
        return (value as T?)?.let { stringValues.indexOf(getString(it)) } ?: -1
    }

    class Test {
        var uno = 1

        private fun Wiii(){

            val objecterino = object {
                var uno: Int = 0
            }.apply {
                uno = 1
            }

            if(this.uno == objecterino.uno){
                //Vykona sa toto?
            } else {
                //Vykona sa toto?
            }
        }
    }

    private fun GiveMeUnoOrInput(unoOrInput: Int? = 1): Int? {
        return unoOrInput
    }

    private fun ViewGroup.FuckOffWithAllViews(){
        val viewGroup: ViewGroup = this
        viewGroup.forEach { view -> viewGroup.removeView(view) }
    }
}