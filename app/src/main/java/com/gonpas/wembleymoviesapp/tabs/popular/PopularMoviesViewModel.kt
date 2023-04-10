package com.gonpas.wembleymoviesapp.tabs.popular

import android.app.Application
//import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.gonpas.wembleymoviesapp.domain.DomainMovie
import com.gonpas.wembleymoviesapp.domain.asMovieDb
import com.gonpas.wembleymoviesapp.network.*
import com.gonpas.wembleymoviesapp.repository.InterfaceMoviesRepository
import com.gonpas.wembleymoviesapp.repository.MoviesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

enum class ApiStatus { LOADING, ERROR, DONE }

//private const val TAG = "xxPmvm"
class PopularMoviesViewModel(val app: Application, private val repository: InterfaceMoviesRepository) : AndroidViewModel(app) {

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    private val _popularMoviesList = MutableLiveData<List<DomainMovie>>()
    val popularMoviesList: LiveData<List<DomainMovie>>
        get() = _popularMoviesList
    private var nextPopPage = 1

    private val _foundMovies = MutableLiveData<List<DomainMovie>>()
    val foundMovies: LiveData<List<DomainMovie>>
        get() = _foundMovies
    private var nextFoundPage = 1


    private val _configuration = MutableLiveData<ImagesDto>()
    private val configuration: LiveData<ImagesDto>
        get() = _configuration

//    private val repository = MoviesRepository(getDatabase(app))


    init {
        downloadConfiguration()
        downloadMovies()
    }


    fun downloadConfiguration(){
        viewModelScope.launch {
            try {
                getConfiguration()
            }catch (ce: CancellationException){
                throw ce
            }catch (e: Exception){
                _status.value = ApiStatus.ERROR
                Toast.makeText(app, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun getConfiguration(){
        _configuration.value = repository.getConfiguration().images
    }

    fun downloadMovies(){
        viewModelScope.launch {
            _status.value = ApiStatus.LOADING
            try {
                getPopularMovies()
            }catch (ce: CancellationException){
                throw ce
            }catch (e: Exception){
                _status.value = ApiStatus.ERROR
                Toast.makeText(app, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    private suspend fun getPopularMovies(){

        if (_popularMoviesList.value.isNullOrEmpty()) {
            _popularMoviesList.value =
                repository.downloadPopMovies(nextPopPage).results.asList()
                    .asListDomainMovies(
                        configuration.value!!.secureBaseUrl,
                        configuration.value!!.posterSizes[2]
                    )
        } else {
            _popularMoviesList.value = _popularMoviesList.value!!.plus(
                repository.downloadPopMovies(nextPopPage).results.asList()
                    .asListDomainMovies(
                        configuration.value!!.secureBaseUrl,
                        configuration.value!!.posterSizes[2]
                    )
            )
        }
        _status.value = ApiStatus.DONE
        nextPopPage ++
    }

    fun saveFavMovie(movie: DomainMovie){
        viewModelScope.launch {
            guardarFav(movie)
        }
    }
    private suspend fun guardarFav(movie: DomainMovie){
        repository.insertFavMovie(movie.asMovieDb())
    }

    fun searchMovies(query: String?){
        if (!query.isNullOrBlank()) {
            viewModelScope.launch {
                _status.value = ApiStatus.LOADING
                try {
                    buscarMovies(query)
                }catch (ce: CancellationException){
                    throw ce
                }catch (e: Exception){
                    _status.value = ApiStatus.ERROR
                    Toast.makeText(app, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun buscarMovies(query: String){
        _foundMovies.value = repository.searchMovie(query, nextFoundPage).results.asList().asListDomainMovies(
            configuration.value!!.secureBaseUrl,
            configuration.value!!.posterSizes[2]
        )
        _status.value = ApiStatus.DONE
        nextFoundPage++
    }

}


@Suppress("UNCHECKED_CAST")
class PopularMoviesViewModelFactory(
    private val app: Application,
    private val moviesRepository: MoviesRepository
    ): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PopularMoviesViewModel::class.java)) {
            return PopularMoviesViewModel(app, moviesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class\nNOT a PopularMoviesViewModel")
    }
}