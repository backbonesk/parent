package sk.backbone.parent.ui.screens

import android.view.View
import androidx.appcompat.widget.Toolbar

interface IToolbarActivity {
    abstract fun setupToolbar()
    abstract fun getToolbarLayoutId(): Int
    abstract fun getToolbarViewId(): Int

    fun createToolbar(activity: ParentActivity<*>) {
        activity.apply {
            if(findViewById<Toolbar>(getToolbarViewId()) == null){
                val toolbar =  View.inflate(this, getToolbarLayoutId(), null)
                getRootView()?.addView(toolbar, 0)
            }

            setupToolbar()
        }
    }
}