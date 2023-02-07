package sk.backbone.parent.ui.screens

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sk.backbone.parent.execution.IExecutioner
import sk.backbone.parent.execution.scopes.ActivityScopes
import javax.inject.Inject

abstract class ParentActivity : ComponentActivity(), IExecutioner<ActivityScopes> {
    open fun getActivityTransitions() : ActivityTransitions = ActivityTransitions.NONE

    inline fun <reified T: ViewModel>getViewModel() : T {
        return ViewModelProvider(this)[T::class.java]
    }

    @Inject override lateinit var scopes: ActivityScopes

    override fun onCreate(savedInstanceState: Bundle?) {
        getActivityTransitions().setStartActivityTransition(this)
        super.onCreate(savedInstanceState)

        if(this is IToolbarActivity<*>){
            refreshToolbar()
        }
    }

    override fun finish() {
        super.finish()
        getActivityTransitions().setFinishActivityTransition(this)
    }

    override fun onDestroy() {
        scopes.cancel()
        super.onDestroy()
    }

    override fun recreate() {
        this.finish()
        this.startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun getRootView(): ViewGroup? {
        return (this.findViewById<View>(android.R.id.content) as ViewGroup?)?.getChildAt(0) as ViewGroup?
    }
}