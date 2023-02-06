package sk.backbone.parent.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding

abstract class ParentViewBindingActivity<TViewBinding: ViewBinding>(private val viewBindingFactory: ((LayoutInflater) -> TViewBinding)?) : ParentActivity() {
    private var _viewBinding: TViewBinding? = null
    val viewBinding: TViewBinding get() = _viewBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _viewBinding = viewBindingFactory?.invoke(layoutInflater)
        _viewBinding?.root?.let { setContentView(it) }
    }

    override fun onDestroy() {
        _viewBinding = null
        super.onDestroy()
    }
}