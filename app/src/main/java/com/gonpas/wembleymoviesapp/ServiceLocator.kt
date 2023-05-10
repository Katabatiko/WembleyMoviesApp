package com.gonpas.wembleymoviesapp

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.gonpas.wembleymoviesapp.database.MoviesDao
import com.gonpas.wembleymoviesapp.database.MoviesDatabase
import com.gonpas.wembleymoviesapp.database.getDatabase
import com.gonpas.wembleymoviesapp.network.TmdbApi
import com.gonpas.wembleymoviesapp.repository.InterfaceMoviesRepository
import com.gonpas.wembleymoviesapp.repository.MoviesRepository

object ServiceLocator {
    private var database: MoviesDatabase? = null
    @Volatile
    var repository: InterfaceMoviesRepository? = null
        @VisibleForTesting set

    private val lock = Any()

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            repository = null
        }
    }

    fun provideMoviesRepository(context: Context): InterfaceMoviesRepository{
        synchronized(this){
            return repository ?: createMoviesRepository(context)
        }
    }
    private fun createMoviesRepository(context: Context): InterfaceMoviesRepository{
        val newRepo = MoviesRepository(TmdbApi.tmdbApiService, createLocalDataSource(context))
        repository = newRepo
        return newRepo
    }

    private fun createLocalDataSource(context: Context): MoviesDao{
        val database = database ?: createDatabase(context)
        return database.movieDao
    }

    private fun createDatabase(context: Context): MoviesDatabase{
        return getDatabase(context)
    }
}