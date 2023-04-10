package com.gonpas.wembleymoviesapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MoviesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovie(movie: MovieDb)

    @Query("select * from MovieDb order by title")
    fun getFavsMovies(): LiveData<List<MovieDb>>

    @Query("delete from MovieDb where movieId = :key")
    suspend fun removeMovieFromDb(key: Int)
}