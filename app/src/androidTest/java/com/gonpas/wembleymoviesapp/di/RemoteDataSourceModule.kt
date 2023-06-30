package com.gonpas.wembleymoviesapp.di

import com.gonpas.wembleymoviesapp.network.FakeAndroidTestRemoteService
import com.gonpas.wembleymoviesapp.network.TmdbApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ApiServiceModule::class]
)
class RemoteDataSourceModule {

    @Singleton
    @Provides
    fun provideFakeRemoteService(): TmdbApiService {
        return FakeAndroidTestRemoteService
    }

}