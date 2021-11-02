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

abstract class ParentSpinner<TViewBinding> : ParentLinearLayout {
    private val viewBindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> TViewBinding

    constructor(viewBindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> TViewBinding, context: Context): this(viewBindingFactory, context, null, 0)
    constructor(viewBindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> TViewBinding, context: Context, attrs: AttributeSet?): this(viewBindingFactory, context, attrs, 0)
    constructor(viewBindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> TViewBinding, context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        this.viewBindingFactory = viewBindingFactory
        init()
    }

    var onItemSelected: ((ParentSpinner<*>, Int) -> Unit)? = null

    var provider: ISpinnerItemsProvider<*>? = null
        private set

    private var _viewBinding: TViewBinding? = null
    val viewBinding: TViewBinding get() = _viewBinding!!

    private var inputValidation = TextInputValidation.NOT_EMPTY
    protected open val spinnerItemResource: Int = android.R.layout.simple_spinner_item
    protected open val spinnerDropdownResource: Int = android.R.layout.simple_spinner_dropdown_item
    abstract val spinner: Spinner

    private fun init() {
        _viewBinding = viewBindingFactory(LayoutInflater.from(context), this, true)

        spinner.isFocusable = true
        spinner.isClickable = true
        spinner.isFocusableInTouchMode = true

        spinner.setOnFocusChangeListener { view, focused ->
            if(focused){
                view.hideKeyboard()
                spinner.performClick()
            }
        }
    }

    inline fun <reified T>setSelection(value: T?, animate: Boolean = true){
        this.provider?.getIndexOf(value)?.let { spinner.setSelection(it, animate) }
    }

    inline fun <reified T>getSelectedItem(): T? {
        return this.provider?.get(spinner.selectedItemPosition) as T?
    }

    private fun setProvider(provider: ISpinnerItemsProvider<*>?, triggerSpinnerListener: Boolean = false){
        val currentItems = provider?.stringValues ?: arrayListOf()

        val adapter = ArrayAdapter(context, spinnerItemResource, currentItems).apply {
            setDropDownViewResource(spinnerDropdownResource)
        }

        spinner.onItemSelectedListener = null
        spinner.adapter = adapter

        if(triggerSpinnerListener){
            setSpinnerListener()
        } else {
            post {
                setSpinnerListener()
            }
        }
    }

    private fun setSpinnerListener() {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onItemSelected?.invoke(this@ParentSpinner, position)
            }
        }
    }

    override fun setEnabled(enabled: Boolean) {
        spinner.isEnabled = enabled
        super.setEnabled(enabled)
    }
}