package sk.backbone.parent.execution

import android.content.Context
import android.view.ViewGroup

interface IExecutioner {
    fun getContext(): Context?
    val scopes: Scopes

    fun getRootView(): ViewGroup?
    
    fun withExecutorParams(execute: (ExecutorParams) -> Unit){
        val context = getContext()
        val rootView = getRootView()

        if(context != null && rootView != null){
            execute(ExecutorParams(rootView, scopes, context))
        }
    }

    fun withExecutorParamsExecute(executorFactoryMethod: (ExecutorParams) -> ParentExecutor<*>) {
        withExecutorParams { executorFactoryMethod(it).execute() }
    }
}