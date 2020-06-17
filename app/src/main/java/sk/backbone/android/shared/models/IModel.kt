package sk.backbone.android.shared.models

import android.content.Context

interface IModel<ModelsProvider> {
    val context: Context
    val modelsProvider: ModelsProvider
}