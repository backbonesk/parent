package sk.backbone.parent.application

import android.app.Service
import android.content.Context
import sk.backbone.parent.execution.Scopes
import sk.backbone.parent.models.IModelsProvider

abstract class ParentService<ModelsProvider: IModelsProvider> : Service(), IModelsProvider {
    val scopes = Scopes()
    abstract val modelsProvider: ModelsProvider
    override val context: Context get() = this

    override fun onDestroy() {
        scopes.cancel()
        super.onDestroy()
    }
}