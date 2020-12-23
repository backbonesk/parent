package sk.backbone.parent.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import sk.backbone.parent.execution.ExecutorParams
import sk.backbone.parent.execution.Scopes

abstract class ParentActivity<TViewBinding: ViewBinding> : AppCompatActivity() {
    val scopes = Scopes()

    private var _viewBinding: TViewBinding? = null
    val viewBinding: TViewBinding get() = _viewBinding!!
    abstract val viewBindingFactory: (LayoutInflater) -> TViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _viewBinding = viewBindingFactory(layoutInflater)
        setContentView(viewBinding.root)

        if(this is IToolbarActivity){
            createToolbar(this)
        }
    }

    override fun onDestroy() {
        scopes.cancelJobs()
        _viewBinding = null
        super.onDestroy()
    }

    override fun recreate() {
        this.finish()
        this.startActivity(intent)
        overridePendingTransition(0, 0)
    }

    fun withExecutorParams(execute: (ExecutorParams) -> Unit){
        getRootView()?.let { ExecutorParams(it, scopes, this) }?.let(execute)
    }

    fun getRootView(): ViewGroup? {
        return (this.findViewById<View>(android.R.id.content) as ViewGroup?)?.getChildAt(0) as ViewGroup?
    }
}