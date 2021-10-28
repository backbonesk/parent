package sk.backbone.parent.execution

import android.content.Context
import android.view.ViewGroup
import sk.backbone.parent.execution.scopes.Scopes

data class ExecutorParams(
    val context: Context,
    val scopes: Scopes,
    val rootView: ViewGroup? = null
    )