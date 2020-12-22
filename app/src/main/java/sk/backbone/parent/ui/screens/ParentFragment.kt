package sk.backbone.parent.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import sk.backbone.parent.execution.ExecutorParams

abstract class ParentFragment: Fragment() {
    val scopes by lazy {
        (this.activity as ParentActivity).scopes
    }

    val uiScope by lazy {
        (this.activity as ParentActivity).scopes.ui
    }

    val ioScope by lazy {
        (this.activity as ParentActivity).scopes.io
    }

    abstract var identifier: String?

    protected abstract val rootViewBindingFactory: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return rootViewBindingFactory(inflater, container, false).apply {
            referenceViewBindings(root)
        }.root
    }

    open fun referenceViewBindings(rootView: View){

    }

    fun withExecutorParams(execute: (ExecutorParams) -> Unit) {
        getRootView()?.let { rootView -> context?.let { context -> ExecutorParams(rootView, scopes, context) } }?.let(execute)
    }

    fun getRootView(): ViewGroup? {
        return this.view as ViewGroup
    }
}