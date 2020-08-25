package sk.backbone.parent.application

import sk.backbone.parent.models.IModelsProvider

interface IService<ModelsProvider> : IModelsProvider {
    val modelsProvider: ModelsProvider
}