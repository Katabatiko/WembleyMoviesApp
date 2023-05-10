package com.gonpas.wembleymoviesapp.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class FakeAndroidTestLocalDataSource: MoviesDao {

    val movieDb1 = MovieDb(
        movieId = 100,
        title = "El test del algoritmo",
        overview = "Probando las funcionalidades del PopularMoviesViewModel para su lanzamiento a producción.",
        releaseDate = "2023-04-08",
        backdropPath = null,
        imgUrl = "http://image.tmdb.org/t/p/w185/poster.jpg",
        popularity = 7.7f,
        voteAverage = 8.8f,
        voteCount = 1000
    )
    private val movieDb2 = MovieDb(
        backdropPath = null,
        movieId = 10002,
        title = "Vientos del norte",
        overview = "Es la historia de Boreal",
        imgUrl = "http://image.tmdb.org/t/p/w185/poster.jpg",
        releaseDate = "2023-3-3",
        popularity = 6.5f,
        voteAverage = 8.8f,
        voteCount = 535
    )
    private val movieDb3 = MovieDb(
        backdropPath = "/3CxUndGhUcZdt1Zggjdb2HkLLQX.jpg",
        movieId = 640146,
        title = "Ant-Man y la Avispa: Quantumanía",
        overview = "La pareja de superhéroes Scott Lang y Hope van Dyne regresa para continuar sus aventuras",
        imgUrl = "http://image.tmdb.org/t/p/w185/jTNYlTEijZ6c8Mn4gvINOeB2HWM.jpg",
        releaseDate = "2023-02-15",
        popularity = 4665.438f,
        voteAverage = 6.573f,
        voteCount = 2301
    )

    private val _observableMovies = MutableLiveData<List<MovieDb>>()

    init {
        _observableMovies.postValue(listOf(
                movieDb1,
                movieDb2,
                movieDb3
            )
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