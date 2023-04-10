package com.gonpas.wembleymoviesapp.repository

import androidx.lifecycle.LiveData
import com.gonpas.wembleymoviesapp.database.MovieDb
import com.gonpas.wembleymoviesapp.database.MoviesDatabase
import com.gonpas.wembleymoviesapp.network.Configuration
import com.gonpas.wembleymoviesapp.network.MoviesListDto
import com.gonpas.wembleymoviesapp.network.TmdbApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MoviesRepository(
    private val database: MoviesDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : InterfaceMoviesRepository {

    override suspend fun downloadPopMovies(page: Int): MoviesListDto{
        return TmdbApi.tmdbApiService.getPopularMovies(page = page)
    }

    override suspend fun searchMovie(query: String, page: Int): MoviesListDto{
        return TmdbApi.tmdbApiService.searchMovie(query= query, page = page)
    }

    override suspend fun getConfiguration(): Configuration{
        return TmdbApi.tmdbApiService.getConfiguration()
    }

    override fun getMoviesFromDb(): LiveData<List<MovieDb>>{
        return database.movieDao.getFavsMovies()
    }

    override suspend fun insertFavMovie(movie: MovieDb){
        withContext(ioDispatcher) {
            database.movieDao.insertMovie(movie)
        }
    }

    override suspend fun removeFavMovie(movieId: Int){
        withContext(ioDispatcher) {
            database.movieDao.removeMovieFromDb(movieId)
        }
    }
}