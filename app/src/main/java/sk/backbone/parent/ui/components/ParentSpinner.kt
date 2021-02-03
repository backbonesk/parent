package sk.backbone.parent.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import sk.backbone.parent.ui.validations.text_validation.TextInputValidation
import sk.backbone.parent.utils.hideKeyboard

abstract class ParentSpinner @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : StateSavingLinearLayout(context, attrs, defStyleAttr) {
    private var onValueChanged: ((String) -> Unit)? = null
    var provider: ISpinnerItemsProvider<*>? = null
        set(value) {
            field = value
            val currentItems = field?.stringValues ?: arrayListOf()

            spinner.adapter = ArrayAdapter(context, spinnerItemResource, currentItems).apply {
                setDropDownViewResource(spinnerDropdownResource)
            }
        }

    private var inputValidation = TextInputValidation.NOT_EMPTY

    protected open val spinnerItemResource: Int = android.R.layout.simple_spinner_item
    protected open val spinnerDropdownResource: Int = android.R.layout.simple_spinner_dropdown_item
    abstract val spinner: Spinner

    init {
        init()
    }

    private fun init() {
        spinner.setOnFocusChangeListener { view, focused ->
            if(focused){
                view.hideKeyboard()
            }
        }

        if(!isEnabled){
            spinner.isEnabled = false
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                provider?.stringValues?.get(position)?.let { onValueChanged?.let { it1 -> it1(it) } }
            }
        }

        spinner.adapter = this.context?.let { ArrayAdapter(it, spinnerItemResource, arrayOf("")) }
    }

    inline fun <reified T>getSelectedItem(): T? {
        return when(val item = this.provider?.get(spinner.selectedItemPosition)){
            item is T -> item as T
            else -> null
        }
    }
}