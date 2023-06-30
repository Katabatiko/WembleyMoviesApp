package com.gonpas.wembleymoviesapp.di

import android.content.Context
import androidx.room.Room
import com.gonpas.wembleymoviesapp.database.MoviesDao
import com.gonpas.wembleymoviesapp.database.MoviesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): MoviesDatabase {
        return Room.databaseBuilder(
            appContext,
            MoviesDatabase::class.java,
            "movies"
        ).build()
    }

    @Provides
    fun provideMoviesDao(database: MoviesDatabase): MoviesDao {
        return database.movieDao
    }
}