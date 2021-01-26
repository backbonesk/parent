package sk.backbone.parent.ui.screens

import android.view.View
import androidx.appcompat.widget.Toolbar

interface IToolbarActivity {
    abstract fun setupToolbar()
    abstract fun getToolbarLayoutId(): Int
    abstract fun getToolbarViewId(): Int

    val ParentActivity<*>.toolbar: Toolbar get() = (findViewById(getToolbarViewId()) ?: View.inflate(this, getToolbarLayoutId(), null).findViewById(getToolbarViewId()))

    fun createToolbar(activity: ParentActivity<*>) {
        activity.apply {
            if(findViewById<Toolbar>(getToolbarViewId()) == null){
                getRootView()?.addView(toolbar, 0)
            }

            setupToolbar()
        }
    }
}