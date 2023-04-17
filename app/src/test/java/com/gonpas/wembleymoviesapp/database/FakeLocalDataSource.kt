package com.gonpas.wembleymoviesapp.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gonpas.wembleymoviesapp.domain.DomainMovie

class FakeLocalDataSource: MoviesDao {

    private val movieDb1 = MovieDb(
        movieId = 100,
        title = "El test del algoritmo",
        overview = "Probando las funcionalidades del PopularMoviesViewModel para su lanzamiento a producción.",
        releaseDate = "2023-04-08",
        backdropPath = null,
        imgUrl = null,
        popularity = 7.7f,
        voteAverage = 8.8f,
        voteCount = 1000
    )
    private val movieDb2 = MovieDb(
        backdropPath = null,
        movieId = 10002,
        title = "Vientos del norte",
        overview = "Es la historia de Boreal",
        imgUrl = null,
        releaseDate = "2023-3-3",
        popularity = 6.5f,
        voteAverage = 8.8f,
        voteCount = 535
    )

    private val _observableMovies = MutableLiveData<List<MovieDb>>()

    init {
        _observableMovies.value = listOf(
            movieDb1,
            movieDb2
        )
    }

    override fun insertMovie(movie: MovieDb) {
        _observableMovies.value = _observableMovies.value!!.plus(movie)
    }

    override fun getFavsMovies(): LiveData<List<MovieDb>> {
        return _observableMovies
    }

    override suspend fun removeMovieFromDb(key: Int) {
        _observableMovies.value = _observableMovies.value!!.filterNot {
            it.movieId == key
        }
    }
}