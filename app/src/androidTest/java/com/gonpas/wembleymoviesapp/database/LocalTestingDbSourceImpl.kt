package com.gonpas.wembleymoviesapp.database

import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import javax.inject.Inject

class LocalTestingDbSourceImpl @Inject constructor(): MoviesDao {
    private val db = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        MoviesDatabase::class.java
    )
        .allowMainThreadQueries()
        .build()

    fun closeDb() {
        db.close()
    }

    override fun insertMovie(movie: MovieDb) {
        db.movieDao.insertMovie(movie)
    }

    override fun getFavsMovies(): LiveData<List<MovieDb>> {
        return db.movieDao.getFavsMovies()
    }

    override suspend fun removeMovieFromDb(key: Int) {
        db.movieDao.removeMovieFromDb(key)
    }
}