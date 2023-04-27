package sk.backbone.parent.application.dependency_injection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.modelmapper.ModelMapper
import org.modelmapper.convention.MatchingStrategies
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ParentModelMapperModule {

    @Singleton
    @Provides
    @ParentModelMapper
    fun provideParentModelMapper(): ModelMapper {
        return ModelMapper().apply {
            configuration.matchingStrategy = MatchingStrategies.STRICT
        }
    }
}


