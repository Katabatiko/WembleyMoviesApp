package com.gonpas.wembleymoviesapp.di

import com.gonpas.wembleymoviesapp.network.TmdbApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

private const val TMBD_BASE_URL = "https://api.themoviedb.org/3/"

@InstallIn(SingletonComponent::class)
@Module
class ApiServiceModule {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(TMBD_BASE_URL)
        .build()

//        @Singleton
//        @get:Provides
//        val tmdbApiService: TmdbApiService = retrofit.create(TmdbApiService::class.java)

    @Singleton
    @Provides
    fun provideTmdbApiService (): TmdbApiService{
        return retrofit.create(TmdbApiService::class.java)
    }
}