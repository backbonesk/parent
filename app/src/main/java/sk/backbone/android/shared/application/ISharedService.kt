package sk.backbone.android.shared.application

import sk.backbone.android.shared.models.IModelsProvider

interface ISharedService<ModelsProvider> : IModelsProvider {
    val modelsProvider: ModelsProvider
}