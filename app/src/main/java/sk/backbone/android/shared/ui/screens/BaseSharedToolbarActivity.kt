package sk.backbone.android.shared.ui.screens

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar

abstract class BaseSharedToolbarActivity : BaseSharedActivity(){
    abstract fun refreshToolbar()
    abstract fun getToolbarLayoutId(): Int
    abstract fun getToolbarViewId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(findViewById<Toolbar>(getToolbarViewId()) == null){
            val toolbar =  View.inflate(this, getToolbarLayoutId(), null)
            getRootView()?.addView(toolbar, 0)
        }

        refreshToolbar()
    }
}