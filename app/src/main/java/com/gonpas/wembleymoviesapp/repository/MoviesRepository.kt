package com.gonpas.wembleymoviesapp.repository

import androidx.lifecycle.LiveData
import com.gonpas.wembleymoviesapp.database.MovieDb
import com.gonpas.wembleymoviesapp.database.MoviesDao
import com.gonpas.wembleymoviesapp.network.Configuration
import com.gonpas.wembleymoviesapp.network.MoviesListDto
import com.gonpas.wembleymoviesapp.network.TmdbApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MoviesRepository(
    private val netService: TmdbApiService,
    private val moviesDao: MoviesDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : InterfaceMoviesRepository {

    override suspend fun downloadPopMovies(page: Int): MoviesListDto{
        return netService.getPopularMovies(page = page)
    }

    override suspend fun searchMovieFromRemote(query: String, page: Int): MoviesListDto{
        return netService.searchMovie(query= query, page = page)
    }

    override suspend fun getConfiguration(): Configuration{
        return netService.getConfiguration()
    }

    override fun getMoviesFromDb(): LiveData<List<MovieDb>>{
        return moviesDao.getFavsMovies()
    }

    override suspend fun insertFavMovie(movie: MovieDb){
        withContext(ioDispatcher) {
            moviesDao.insertMovie(movie)
        }
    }

    override suspend fun removeFavMovie(movieId: Int){
        withContext(ioDispatcher) {
            moviesDao.removeMovieFromDb(movieId)
        }
    }
}