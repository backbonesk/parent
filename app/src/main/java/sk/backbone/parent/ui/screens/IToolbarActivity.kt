package sk.backbone.parent.ui.screens

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

interface IToolbarActivity<ToolbarViewBinding: ViewBinding> {
    abstract fun refreshToolbar()

    val toolbarRootId: Int
    val toolbarRootInflater: (Context, ViewGroup?, Boolean) -> ToolbarViewBinding
    val toolbarRootBinder: (View) -> ToolbarViewBinding

    val toolbarViewBinding: ToolbarViewBinding get () {
        if(this is ParentActivity<*>){
            val activityRootView = this.getRootView()

            return if(activityRootView != null){
                val toolbarRoot = activityRootView.findViewById<ViewGroup>(toolbarRootId)

                if(toolbarRoot == null){
                    val viewBinding = toolbarRootInflater(this, activityRootView, false)
                    activityRootView.addView(viewBinding.root, 0)
                    viewBinding
                } else {
                    toolbarRootBinder(toolbarRoot)
                }
            } else {
                throw Exception("Root of activity is not available. Can not bind or inlate toolbar layout.")
            }
        }
        else {
            throw Exception("IToolbarActivity can be only used with ParentActivity<*>")
        }
    }
}