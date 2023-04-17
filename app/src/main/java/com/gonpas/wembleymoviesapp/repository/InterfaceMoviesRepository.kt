package com.gonpas.wembleymoviesapp.repository

import androidx.lifecycle.LiveData
import com.gonpas.wembleymoviesapp.database.MovieDb
import com.gonpas.wembleymoviesapp.network.Configuration
import com.gonpas.wembleymoviesapp.network.MoviesListDto

interface InterfaceMoviesRepository {
    suspend fun downloadPopMovies(page: Int): MoviesListDto
    suspend fun searchMovieFromRemote(query: String, page: Int): MoviesListDto
    suspend fun getConfiguration(): Configuration
    fun getMoviesFromDb(): LiveData<List<MovieDb>>
    suspend fun insertFavMovie(movie: MovieDb)
    suspend fun removeFavMovie(movieId: Int)
}