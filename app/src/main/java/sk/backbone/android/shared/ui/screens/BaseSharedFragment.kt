package sk.backbone.android.shared.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sk.backbone.android.shared.execution.ExecutorParams

abstract class BaseSharedFragment: Fragment() {
    val scopes by lazy {
        (this.activity as BaseSharedActivity).scopes
    }

    val uiScope by lazy {
        (this.activity as BaseSharedActivity).scopes.ui
    }

    val ioScope by lazy {
        (this.activity as BaseSharedActivity).scopes.io
    }

    abstract var identifier: String?
    abstract fun getFragmentLayout(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getFragmentLayout(), container, false)
    }

    protected fun getExecutorParams(): ExecutorParams? {
        return getRootView()?.let { rootView ->
            context?.let { context ->
                ExecutorParams(rootView, scopes, context)
            }
        }
    }

    fun getRootView(): ViewGroup? {
        return this.view as ViewGroup
    }
}