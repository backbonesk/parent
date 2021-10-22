package sk.backbone.parent.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.isActive
import sk.backbone.parent.execution.IExecutioner
import sk.backbone.parent.execution.scopes.FragmentScopes
import sk.backbone.parent.execution.scopes.Scopes

abstract class ParentFragment<TViewBinding: ViewBinding>(private val viewBindingFactory: ((LayoutInflater, ViewGroup?, Boolean) -> TViewBinding)?): Fragment(), IExecutioner<FragmentScopes> {
    override var scopes = FragmentScopes()

    inline fun <reified T: ViewModel>getViewModel() : T {
        return ViewModelProvider(this)[T::class.java]
    }

    open var identifier: String? = this::class.java.toString()

    private var _viewBinding: TViewBinding? = null
    val viewBinding: TViewBinding get() = _viewBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _viewBinding = viewBindingFactory?.invoke(inflater, container, false)

        if(!scopes.default.isActive || !scopes.io.isActive || !scopes.ui.isActive){
            scopes = FragmentScopes()
        }


        return viewBinding.root
    }

    override fun onDestroyView() {
        scopes.cancel()
        _viewBinding = null

        super.onDestroyView()
    }

    override fun getRootView(): ViewGroup {
        return this.requireView() as ViewGroup
    }
}