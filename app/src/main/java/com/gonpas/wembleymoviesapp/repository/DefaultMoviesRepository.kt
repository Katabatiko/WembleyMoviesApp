package com.gonpas.wembleymoviesapp.repository

import androidx.lifecycle.LiveData
import com.gonpas.wembleymoviesapp.database.MovieDb
import com.gonpas.wembleymoviesapp.database.MoviesDao
import com.gonpas.wembleymoviesapp.di.Despachadores
import com.gonpas.wembleymoviesapp.network.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultMoviesRepository @Inject constructor(
    private val netService: TmdbApiService,
    private val moviesDao: MoviesDao,
    @Despachadores.IO private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MoviesRepository {

    override suspend fun getConfiguration(): Configuration{
        return netService.getConfiguration()
    }

    override suspend fun downloadPopMovies(page: Int): MoviesListDto{
        return netService.getPopularMovies(page = page)
    }

    override suspend fun searchMovieFromRemote(query: String, page: Int): MoviesListDto{
        return netService.searchMovie(query = query, page = page)
    }

    override suspend fun getMovieCredits(movieId: Int): CreditsDto {
        return netService.getMovieCredits(movieId)
    }

    override suspend fun getPerson(personId: Int): PersonDto {
        return netService.getPerson(personId)
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