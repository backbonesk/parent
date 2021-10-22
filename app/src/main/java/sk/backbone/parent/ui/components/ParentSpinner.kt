package sk.backbone.parent.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import sk.backbone.parent.ui.validations.text_validation.TextInputValidation
import sk.backbone.parent.utils.hideKeyboard

abstract class ParentSpinner<TViewBinding> @JvmOverloads constructor(private val viewBindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> TViewBinding, context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ParentLinearLayout(context, attrs, defStyleAttr) {
    var onItemSelected: ((ParentSpinner<*>, Int) -> Unit)? = null
    var provider: ISpinnerItemsProvider<*>? = null
        set(value) {
            field = value
            val currentItems = field?.stringValues ?: arrayListOf()

            spinner.adapter = ArrayAdapter(context, spinnerItemResource, currentItems).apply {
                setDropDownViewResource(spinnerDropdownResource)
            }
        }

    private var _viewBinding: TViewBinding? = null
    val viewBinding: TViewBinding get() = _viewBinding!!

    private var inputValidation = TextInputValidation.NOT_EMPTY
    protected open val spinnerItemResource: Int = android.R.layout.simple_spinner_item
    protected open val spinnerDropdownResource: Int = android.R.layout.simple_spinner_dropdown_item
    abstract val spinner: Spinner

    init {
        init()
    }

    private fun init() {
        _viewBinding = viewBindingFactory(LayoutInflater.from(context), this, true)

        spinner.isFocusable = true
        spinner.isClickable = true
        spinner.isFocusableInTouchMode = true

        spinner.adapter = this.context?.let { ArrayAdapter(it, spinnerItemResource, arrayOf("")) }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onItemSelected?.invoke(this@ParentSpinner, position)
            }
        }

        spinner.setOnFocusChangeListener { view, focused ->
            if(focused){
                view.hideKeyboard()
                spinner.performClick()
            }
        }
    }

    inline fun <reified T>setSelection(value: T?){
        this.provider?.getIndexOf(value)?.let { spinner.setSelection(it, true) }
    }

    inline fun <reified T>getSelectedItem(): T? {
        return this.provider?.get(spinner.selectedItemPosition) as T?
    }

    override fun setEnabled(enabled: Boolean) {
        spinner.isEnabled = enabled
        super.setEnabled(enabled)
    }
}