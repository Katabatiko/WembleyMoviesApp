package com.gonpas.wembleymoviesapp.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class FakeLocalDataSource: MoviesDao {

    private val movieDb1 = MovieDb(
        movieId = 100,
        title = "El test del algoritmo",
        overview = "Probando las funcionalidades de Room.",
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

    private var movies = listOf<MovieDb>()
    private var _observableMovies = MutableLiveData<List<MovieDb>>()

    init {
        movies = listOf(
            movieDb1,
            movieDb2
        )
        refreshObservableMovies()
    }

    override fun insertMovie(movie: MovieDb) {
        movies = movies.plus(movie)
        refreshObservableMovies()
    }

    override fun getFavsMovies(): LiveData<List<MovieDb>> {
        return _observableMovies
    }

    override suspend fun removeMovieFromDb(key: Int) {
        movies = movies.filterNot {
            it.movieId == key
        }
        refreshObservableMovies()
    }

    private fun refreshObservableMovies() {
        _observableMovies.postValue(movies)
    }
}