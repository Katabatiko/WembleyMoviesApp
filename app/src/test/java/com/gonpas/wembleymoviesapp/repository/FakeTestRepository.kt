package com.gonpas.wembleymoviesapp.repository

import androidx.lifecycle.LiveData
import com.gonpas.wembleymoviesapp.database.FakeLocalDataSource
import com.gonpas.wembleymoviesapp.database.MovieDb
import com.gonpas.wembleymoviesapp.network.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

private const val TAG = "xxFmrTest"

@ExperimentalCoroutinesApi
class FakeTestRepository(): MoviesRepository {

    private val remoteDataSource = FakeRemoteService
    private val localDataSource = FakeLocalDataSource()

    // para la simulacion de errores
    fun shouldReturnError(value: Boolean){
        remoteDataSource.setReturnError(value)
    }

    override suspend fun getConfiguration(): Configuration {
        return remoteDataSource.getConfiguration()
    }

    override suspend fun downloadPopMovies(page: Int): MoviesListDto {
        return  remoteDataSource.getPopularMovies()
    }

    override suspend fun searchMovieFromRemote(query: String, page: Int): MoviesListDto {
        return remoteDataSource.searchMovie(query= query, page = page)
    }

    override suspend fun getMovieCredits(movieId: Int): CreditsDto {
        return remoteDataSource.getMovieCredits(movieId)
    }

    override suspend fun getPerson(personId: Int): PersonDto {
        return remoteDataSource.getPerson(personId)
    }

    override fun getMoviesFromDb(): LiveData<List<MovieDb>> {
        return localDataSource.getFavsMovies()
    }

    override suspend fun insertFavMovie(movie: MovieDb){
        localDataSource.insertMovie(movie)
    }

    override suspend fun removeFavMovie(movieId: Int) {
        localDataSource.removeMovieFromDb(movieId)
    }
}

