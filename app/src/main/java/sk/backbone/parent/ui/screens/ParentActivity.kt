package sk.backbone.parent.ui.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import sk.backbone.parent.execution.IExecutioner
import sk.backbone.parent.execution.Scopes

abstract class ParentActivity<TViewBinding: ViewBinding>(private val viewBindingFactory: ((LayoutInflater) -> TViewBinding)?) : AppCompatActivity(), IExecutioner {
    open fun getActivityTransitions() : ActivityTransitions = ActivityTransitions.NONE

    inline fun <reified T: ViewModel>getViewModel() : T {
        return ViewModelProvider(this)[T::class.java]
    }

    override val scopes = Scopes()

    private var _viewBinding: TViewBinding? = null
    val viewBinding: TViewBinding get() = _viewBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        getActivityTransitions().setStartActivityTransition(this)
        super.onCreate(savedInstanceState)

        _viewBinding = viewBindingFactory?.invoke(layoutInflater)
        _viewBinding?.root?.let { setContentView(it) }

        if(this is IToolbarActivity){
            createToolbar(this)
        }
    }

    override fun finish() {
        super.finish()
        getActivityTransitions().setFinishActivityTransition(this)
    }

    override fun onDestroy() {
        scopes.cancel()
        _viewBinding = null
        super.onDestroy()
    }

    override fun recreate() {
        this.finish()
        this.startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun getRootView(): ViewGroup? {
        return (this.findViewById<View>(android.R.id.content) as ViewGroup?)?.getChildAt(0) as ViewGroup?
    }

    override fun getContext(): Context? = this
}