package sk.backbone.parent.application.dependency_injection

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.logging.HttpLoggingInterceptor
import sk.backbone.parent.repositories.server.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ParentServerRepositoryModule {

    @ParentDefaultHttpClient
    @Singleton
    @Provides
    fun provideParentDefaultHttpClient(@ApplicationContext context: Context): HttpClient  {
        return HttpClient(context)
    }

    @ParentDefaultHttpClientLogging
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

}

