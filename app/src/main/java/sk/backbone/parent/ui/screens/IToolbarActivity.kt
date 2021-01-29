package sk.backbone.parent.ui.screens

import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar

interface IToolbarActivity {
    abstract fun setupToolbar()
    abstract fun getToolbarLayoutId(): Int
    abstract fun getToolbarViewId(): Int

    val ParentActivity<*>.toolbar: Toolbar? get() = findViewById(getToolbarViewId())

    fun createToolbar(activity: ParentActivity<*>) {
        activity.apply {
            if(toolbar == null){
                getRootView()?.addView(LayoutInflater.from(this).inflate(getToolbarLayoutId(), getRootView(), false), 0)
            }

            setupToolbar()
        }
    }
}