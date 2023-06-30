package com.gonpas.wembleymoviesapp.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gonpas.wembleymoviesapp.database.StubLocalData.movieDb1
import com.gonpas.wembleymoviesapp.database.StubLocalData.movieDb2
import com.gonpas.wembleymoviesapp.database.StubLocalData.movieDb3
import javax.inject.Inject

private const val TAG = "xxFatlds"
class FakeAndroidTestLocalDataSource @Inject constructor(): MoviesDao {

    private var movies = listOf<MovieDb>()
    private val _observableMovies = MutableLiveData<List<MovieDb>>()

    init {
        movies = movies.plus(listOf(
                movieDb1,
                movieDb2,
                movieDb3
            ))
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
