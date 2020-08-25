package sk.backbone.parent.execution

import android.content.Context
import android.view.ViewGroup

data class ExecutorParams(
    val rootView: ViewGroup,
    val scopes: Scopes,
    val context: Context
)