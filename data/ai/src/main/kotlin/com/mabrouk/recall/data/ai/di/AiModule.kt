package com.mabrouk.recall.data.ai.di

import com.mabrouk.recall.data.ai.DefaultModelManager
import com.mabrouk.recall.data.ai.ModelManager
import com.mabrouk.recall.data.ai.download.ModelDownloadWorker
import com.mabrouk.recall.data.ai.model.DefaultModelRegistry
import com.mabrouk.recall.data.ai.model.ModelRegistry
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiBindingsModule {

    @Binds
    @Singleton
    abstract fun bindModelRegistry(impl: DefaultModelRegistry): ModelRegistry

    @Binds
    @Singleton
    abstract fun bindModelManager(impl: DefaultModelManager): ModelManager
}

@Module
@InstallIn(SingletonComponent::class)
object AiProvidesModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = ModelDownloadWorker.defaultOkHttpClient()
}
