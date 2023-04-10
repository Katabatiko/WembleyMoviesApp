package com.gonpas.wembleymoviesapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gonpas.wembleymoviesapp.database.MovieDb
import com.gonpas.wembleymoviesapp.network.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

private const val TAG = "xxFmrTest"

@ExperimentalCoroutinesApi
class FakeMoviesRepository: InterfaceMoviesRepository {

    // stub de la database
    private val _observableMovies = MutableLiveData<List<MovieDb>>()
    // stub del network service
    private val actualMoviesDto = moviesDto

    init {
        _observableMovies.value = listOf(movieDb1, movieDb2)
    }

    override suspend fun downloadPopMovies(page: Int): MoviesListDto {
        return actualMoviesDto
    }

    override suspend fun searchMovie(query: String, page: Int): MoviesListDto {
        val newResult =  actualMoviesDto.results.filter {
            it.title.contains(query)
        }
        val results = newResult.toTypedArray()
//        val results = arrayOf(newResult[0], newResult[1])
        return MoviesListDto(
            page= page,
            results = results,
            totalPages = 1,
            totalResults = newResult.size
//            totalResults = 1
        )
    }

    override suspend fun getConfiguration(): Configuration {
        return conf
    }

    override fun getMoviesFromDb(): LiveData<List<MovieDb>> {
        return _observableMovies
    }

    override suspend fun insertFavMovie(movie: MovieDb){
        _observableMovies.value = _observableMovies.value!!.plus(movie)
    }

    override suspend fun removeFavMovie(movieId: Int) {
        _observableMovies.value = _observableMovies.value!!.filterNot {
            it.movieId == movieId
        }
    }
}

