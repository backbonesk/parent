package sk.backbone.parent.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import sk.backbone.parent.execution.ExecutorParams
import sk.backbone.parent.execution.ParentExecutor
import sk.backbone.parent.execution.Scopes

abstract class ParentFragment<TViewBinding: ViewBinding>: Fragment() {
    val scopes = Scopes()

    abstract var identifier: String?

    private var _viewBinding: TViewBinding? = null
    val viewBinding: TViewBinding get() = _viewBinding!!
    abstract val viewBindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> TViewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _viewBinding = viewBindingFactory(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        scopes.cancelJobs()
        _viewBinding = null

        super.onDestroyView()
    }

    fun withExecutorParams(execute: (ExecutorParams) -> Unit) {
        context?.let { context -> ExecutorParams(getRootView(), scopes, context) }?.let(execute)
    }

    fun withExecutorParamsExecute(executorFactoryMethod: (ExecutorParams) -> ParentExecutor<*>) {
        context?.let { context -> ExecutorParams(getRootView(), scopes, context) }?.let(executorFactoryMethod)?.execute()
    }

    fun getRootView(): ViewGroup {
        return this.requireView() as ViewGroup
    }
}