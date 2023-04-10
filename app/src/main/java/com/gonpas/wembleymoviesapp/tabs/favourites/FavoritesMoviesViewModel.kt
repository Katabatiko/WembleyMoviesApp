package com.gonpas.wembleymoviesapp.tabs.favourites

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.gonpas.wembleymoviesapp.database.asListDomainMovies
import com.gonpas.wembleymoviesapp.database.getDatabase
import com.gonpas.wembleymoviesapp.domain.DomainMovie
import com.gonpas.wembleymoviesapp.repository.InterfaceMoviesRepository
import com.gonpas.wembleymoviesapp.repository.MoviesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesMoviesViewModel(
    val app: Application,
    private val repository: InterfaceMoviesRepository
) : AndroidViewModel(app) {

//    private val repository = MoviesRepository(getDatabase(app))
    val favsMovies: LiveData<List<DomainMovie>> = repository.getMoviesFromDb().asListDomainMovies()


    fun removeMovie(movieId: Int){
        viewModelScope.launch {
            repository.removeFavMovie(movieId)
        }
    }


    /*val avisoVisibility = favsMovies.map {
        it.isEmpty()
    }*/
}


@Suppress("UNCHECKED_CAST")
class FavoritesMoviesViewModelFactory(
    private val application: Application,
    private val repository: InterfaceMoviesRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesMoviesViewModel::class.java)) {
            return FavoritesMoviesViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class\nNOT a FavoritesMoviesViewModel")
    }
}