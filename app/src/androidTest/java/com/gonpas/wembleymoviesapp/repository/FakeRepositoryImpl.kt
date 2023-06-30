package com.gonpas.wembleymoviesapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.gonpas.wembleymoviesapp.database.MovieDb
import com.gonpas.wembleymoviesapp.database.MoviesDao
import com.gonpas.wembleymoviesapp.di.Despachadores
import com.gonpas.wembleymoviesapp.network.Configuration
import com.gonpas.wembleymoviesapp.network.CreditsDto
import com.gonpas.wembleymoviesapp.network.FakeAndroidTestRemoteService
import com.gonpas.wembleymoviesapp.network.MoviesListDto
import com.gonpas.wembleymoviesapp.network.PersonDto
import com.gonpas.wembleymoviesapp.network.TmdbApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FakeRepositoryImpl @Inject constructor(
    val remoteSourceData: TmdbApiService,
    val localSourceData: MoviesDao,
    @Despachadores.IO val ioDispatcher: CoroutineDispatcher
): MoviesRepository {
    // para la simulacion de errores
    fun setReturnError(value: Boolean){
        Log.d("xxFri","debe devolver error: $value")
        (remoteSourceData as FakeAndroidTestRemoteService).setReturnError(value)
    }


    override suspend fun downloadPopMovies(page: Int): MoviesListDto {
        Log.d("xxFri","remoteSourceData: $remoteSourceData")
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

    override suspend fun insertFavMovie(movie: MovieDb) {
        withContext(ioDispatcher) {
            localSourceData.insertMovie(movie)
        }
    }

    override suspend fun removeFavMovie(movieId: Int) {
        withContext(ioDispatcher) {
            localSourceData.removeMovieFromDb(movieId)
        }
    }
}