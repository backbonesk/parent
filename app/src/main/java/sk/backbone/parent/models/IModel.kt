package sk.backbone.parent.models

import android.content.Context

interface IModel<ModelsProvider> where ModelsProvider : IModelsProvider {
    val context: Context
    val modelsProvider: ModelsProvider
}