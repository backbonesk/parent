package sk.backbone.android.shared.application

import sk.backbone.android.shared.models.IModelsProvider

interface IService<ModelsProvider> : IModelsProvider {
    val modelsProvider: ModelsProvider
}