package sk.backbone.parent.execution

import android.content.Context
import android.view.ViewGroup
import sk.backbone.parent.application.services.ParentFcmService
import sk.backbone.parent.application.services.ParentService
import sk.backbone.parent.execution.scopes.Scopes
import sk.backbone.parent.ui.screens.ParentActivity
import sk.backbone.parent.ui.screens.ParentFragment
import sk.backbone.parent.ui.screens.ParentRecyclerAdapter

interface IExecutioner<TScopes: Scopes> {
    private val _context: Context? get(){
        return when (this) {
            is ParentFragment<*> -> context
            is ParentActivity<*> -> this
            is ParentService -> this
            is ParentFcmService -> this
            is Context -> this
            is ParentRecyclerAdapter<*, *> -> this.context
            else -> null
        }
    }

    var scopes: TScopes

    fun getRootView(): ViewGroup? = null
    
    fun withExecutorParams(execute: (ExecutorParams) -> Unit){
        val context = _context

        val rootView = getRootView()

        if(context != null){
            execute(ExecutorParams(context, scopes, rootView))
        }
    }

    fun withExecutorParamsExecute(executorFactoryMethod: (ExecutorParams) -> ParentExecutor<*>) {
        withExecutorParams { executorFactoryMethod(it).execute() }
    }

    fun withExecutorParamsExecuteMultiple(executorsFactoryMethod: (ExecutorParams) -> Array<ParentExecutor<*>>) {
        withExecutorParams { params -> executorsFactoryMethod(params).forEach { executor -> executor.execute() } }
    }
}