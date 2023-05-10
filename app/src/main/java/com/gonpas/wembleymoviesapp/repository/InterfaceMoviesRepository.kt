package com.gonpas.wembleymoviesapp.repository

import androidx.lifecycle.LiveData
import com.gonpas.wembleymoviesapp.database.MovieDb
import com.gonpas.wembleymoviesapp.network.Configuration
import com.gonpas.wembleymoviesapp.network.CreditsDto
import com.gonpas.wembleymoviesapp.network.MoviesListDto
import com.gonpas.wembleymoviesapp.network.PersonDto

interface InterfaceMoviesRepository {
    suspend fun getConfiguration(): Configuration
    suspend fun downloadPopMovies(page: Int): MoviesListDto
    suspend fun searchMovieFromRemote(query: String, page: Int): MoviesListDto
    suspend fun getMovieCredits(movieId: Int): CreditsDto
    suspend fun getPerson(personId: Int): PersonDto
    fun getMoviesFromDb(): LiveData<List<MovieDb>>
    suspend fun insertFavMovie(movie: MovieDb)
    suspend fun removeFavMovie(movieId: Int)
}