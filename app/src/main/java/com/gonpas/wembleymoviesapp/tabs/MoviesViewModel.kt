package com.gonpas.wembleymoviesapp.tabs

import android.app.Application
//import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.gonpas.wembleymoviesapp.R
import com.gonpas.wembleymoviesapp.database.asListDomainMovies
import com.gonpas.wembleymoviesapp.domain.DomainMovie
import com.gonpas.wembleymoviesapp.domain.asMovieDb
import com.gonpas.wembleymoviesapp.network.*
import com.gonpas.wembleymoviesapp.repository.InterfaceMoviesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

enum class ApiStatus { LOADING, ERROR, DONE }

private const val TAG = "xxMvm"
class MoviesViewModel(val app: Application, private val repository: InterfaceMoviesRepository) : AndroidViewModel(app) {

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    private val _popularMoviesList = MutableLiveData<List<DomainMovie>>()
    val popularMoviesList: LiveData<List<DomainMovie>>
        get() = _popularMoviesList
    var totalMovies = 0
    private var nextPopPage = 1
    private var lastPopPage = 1

    private val _foundMovies = MutableLiveData<List<DomainMovie>>()
    val foundMovies: LiveData<List<DomainMovie>>
        get() = _foundMovies
    var found = 0
    var nextFoundPage = 1
    private var lastFoundPage = 1
    private var lastSuccessQuery = ""

    private val _configuration = MutableLiveData<ImagesDto>()
    val configuration: LiveData<ImagesDto>
        get() = _configuration


    /* FAVORITES */
    val favsMovies: LiveData<List<DomainMovie>> = repository.getMoviesFromDb().asListDomainMovies()



    init {
//        _popularMoviesList.value = listOf()
        downloadConfiguration()
        downloadMovies()
    }
    /**
     * Se restablecen las variables de paginación
     * de la busqueda a valor por defecto '1':
     * nextFoundPage
     * lastFoundPage
     */
    fun resetSearchControls(){
        nextFoundPage = 1
        lastFoundPage = 1
    }


    private fun downloadConfiguration(){
        viewModelScope.launch {
            try {
                getConfiguration()
            }catch (ce: CancellationException){
                throw ce
            }catch (e: Exception){
                _status.postValue(ApiStatus.ERROR)
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

        if (nextPopPage <= lastPopPage) {
            val moviesLIstDto = repository.downloadPopMovies(nextPopPage)
            lastPopPage = moviesLIstDto.totalPages
            totalMovies = moviesLIstDto.totalResults

            if(!_popularMoviesList.isInitialized){
                _popularMoviesList.value = moviesLIstDto.results.asList()
                        .asListDomainMovies(
                            configuration.value!!.secureBaseUrl,
                            configuration.value!!.posterSizes[2]
                        )
            } else {
                _popularMoviesList.postValue(
                    _popularMoviesList.value!!.plus(
                        moviesLIstDto.results.asList()
                            .asListDomainMovies(
                                configuration.value!!.secureBaseUrl,
                                configuration.value!!.posterSizes[2]
                            )
                    )
                )
            }

            nextPopPage++
        } else {
            Toast.makeText(app, app.getText(R.string.noMas), Toast.LENGTH_LONG).show()
        }
        _status.value = ApiStatus.DONE
    }

    fun evalFavsInPops(parcial: Boolean) {
//        Log.d(TAG,"evaluando favoritos en populares: parcial $parcial")
        if (parcial) {
            val lastPos = _popularMoviesList.value!!.size
            val rangoInf =  if (lastPos < 20)   0
                            else                lastPos - 20

            _popularMoviesList.value!!.subList(rangoInf, lastPos).forEach { pop ->
//                Log.d(TAG,"titulo en parcial: ${pop.title} -> ${pop.id}")
                favsMovies.value!!.forEach {
                    if (it.id == pop.id) {
                        pop.fav = true
//                        Log.d(TAG,"favorita: ${it.title}")
                    }
                }
            }
        } else {
            _popularMoviesList.value!!.forEach { pop ->
//                Log.d(TAG,"pop.title: ${pop.title} -> ${pop.id}")
                favsMovies.value!!.forEach {
                    pop.fav = it.id == pop.id
//                    Log.d(TAG,"fav.id: ${it.id} - pop.fav: ${pop.fav}")

                }
            }
        }
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
        var newQuery = lastSuccessQuery != query
        if (newQuery)      resetSearchControls()

        if (nextFoundPage <= lastFoundPage) {
            val moviesListDto = repository.searchMovieFromRemote(query, nextFoundPage++)
            found = moviesListDto.totalResults
            lastFoundPage = moviesListDto.totalPages
            if( newQuery ) {
                _foundMovies.value = moviesListDto.results.asList()
                    .asListDomainMovies(
                        configuration.value!!.secureBaseUrl,
                        configuration.value!!.posterSizes[2]
                    )
                if (_foundMovies.value!!.isNotEmpty()) {
                    lastSuccessQuery = query
                    newQuery = false
                }
            } else {
                _foundMovies.value = _foundMovies.value!!.plus(
                    moviesListDto.results.asList().asListDomainMovies(
                        configuration.value!!.secureBaseUrl,
                        configuration.value!!.posterSizes[2]
                    )
                )
            }
        } else {
            Toast.makeText(app, app.getText(R.string.noMas), Toast.LENGTH_LONG).show()
//            Log.d(TAG,"busqueda sin resultados")
        }
        _status.value = ApiStatus.DONE
//        Log.d(TAG, "newQuery: $newQuery")
//        Log.d(TAG,"lastSuccessQuery: $lastSuccessQuery")
//        Log.d(TAG, "nextFoundPage: $nextFoundPage")
//        Log.d(TAG, "lastFoundPage: $lastFoundPage")
    }

    val noFoundVisibility = _foundMovies.map {
        it.isEmpty()
    }


    /* FAFORITES */
    fun removeMovie(movieId: Int){
        viewModelScope.launch {
            repository.removeFavMovie(movieId)
        }
    }

    val noHayFavorites = favsMovies.map {
        it.isEmpty()
    }

}


@Suppress("UNCHECKED_CAST")
class MoviesViewModelFactory(
    private val app: Application,
    private val moviesRepository: InterfaceMoviesRepository
    ): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoviesViewModel::class.java)) {
            return MoviesViewModel(app, moviesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class\nNOT a PopularMoviesViewModel")
    }
}