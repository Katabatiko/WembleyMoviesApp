package com.gonpas.wembleymoviesapp.di

import com.gonpas.wembleymoviesapp.repository.DefaultMoviesRepository
import com.gonpas.wembleymoviesapp.repository.MoviesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Singleton


@InstallIn(ViewModelComponent::class)
@Module
abstract class RepositoryModule {
//    @Singleton -> solo lo instancia el viewModel
    @Binds
    abstract fun bindRepository(impl: DefaultMoviesRepository): MoviesRepository
}