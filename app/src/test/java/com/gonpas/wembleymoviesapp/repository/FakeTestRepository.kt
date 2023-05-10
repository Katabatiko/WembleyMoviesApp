package com.gonpas.wembleymoviesapp.repository

import androidx.lifecycle.LiveData
import com.gonpas.wembleymoviesapp.database.FakeLocalDataSource
import com.gonpas.wembleymoviesapp.database.MovieDb
import com.gonpas.wembleymoviesapp.network.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

private const val TAG = "xxFmrTest"

@ExperimentalCoroutinesApi
class FakeMoviesRepository(): InterfaceMoviesRepository {

    private val remoteSourceData = FakeRemoteService
    private val localSourceData = FakeLocalDataSource()


    override suspend fun downloadPopMovies(page: Int): MoviesListDto {
        return remoteSourceData.getPopularMovies()
    }

    override suspend fun searchMovieFromRemote(query: String, page: Int): MoviesListDto {
        return remoteSourceData.searchMovie(query= query, page = page)
    }

    override suspend fun getMovieCredits(movieId: Int): CreditsDto {
        return remoteSourceData.getMovieCredits(movieId)
    }

    override suspend fun getPerson(personId: Int): PersonDto {
        return remoteSourceData.getPerson(personId)
    }

    override suspend fun getConfiguration(): Configuration {
        return remoteSourceData.getConfiguration()
    }

    override fun getMoviesFromDb(): LiveData<List<MovieDb>> {
        return localSourceData.getFavsMovies()
    }

    override suspend fun insertFavMovie(movie: MovieDb){
        localSourceData.insertMovie(movie)
    }

    override suspend fun removeFavMovie(movieId: Int) {
        localSourceData.removeMovieFromDb(movieId)
    }
}

