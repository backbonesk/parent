package sk.backbone.android.shared.ui.screens

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import sk.backbone.android.shared.execution.ExecutorParams
import sk.backbone.android.shared.execution.Scopes

abstract class BaseSharedActivity : AppCompatActivity() {
    abstract fun getActivityLayoutId(): Int?

    val scopes = Scopes()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getActivityLayoutId()?.let { setContentView(it) }

        if(this is IToolbarActivity){
            createToolbar(this)
        }
    }

    override fun onDestroy() {
        scopes.cancelJobs()
        super.onDestroy()
    }

    override fun recreate() {
        this.finish()
        this.startActivity(intent)
        overridePendingTransition(0, 0)
    }

    fun withExecutorParams(execute: (ExecutorParams) -> Unit){
        getRootView()?.let { ExecutorParams(it, scopes, this) }?.let(execute)
    }

    fun getRootView(): ViewGroup? {
        return (this.findViewById<View>(android.R.id.content) as ViewGroup?)?.getChildAt(0) as ViewGroup?
    }
}