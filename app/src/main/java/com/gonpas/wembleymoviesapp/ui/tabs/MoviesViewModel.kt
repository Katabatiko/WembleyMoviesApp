package com.gonpas.wembleymoviesapp.ui.tabs

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.gonpas.wembleymoviesapp.R
import com.gonpas.wembleymoviesapp.database.asLiveDataListDomainMovies
import com.gonpas.wembleymoviesapp.domain.*
import com.gonpas.wembleymoviesapp.network.*
import com.gonpas.wembleymoviesapp.repository.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ApiStatus { LOADING, ERROR, DONE }

private const val TAG = "xxMvm"


@HiltViewModel
class MoviesViewModel
    @Inject constructor(
        val app: Application,
        private val repository: MoviesRepository,
        val savedState: SavedStateHandle
) : AndroidViewModel(app) {

    // LiveDatas
    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status
    fun resetStatus(){
        _status.value = ApiStatus.DONE
    }

    var configuration = MutableLiveData<DomainImages> ()
    fun getConf() = savedState.getLiveData<DomainImages>("conf")

    private var _popularMoviesList = MutableLiveData<List<DomainMovie>>()
    fun getPops() = savedState.getLiveData<List<DomainMovie>>("popMovies")
    var totalMovies = 0
    private var nextPopPage = 1
    private var lastPopPage = 1

    private var _foundMovies = MutableLiveData<List<DomainMovie>?>()
    fun getFound() = savedState.getLiveData<List<DomainMovie>?>("foundMovies")
    var found = 0
    var nextFoundPage = 1
    private var lastFoundPage = 1
    private var lastSuccessQuery = ""



    /* FAVORITES */
    var favsMovies: LiveData<List<DomainMovie>> = repository.getMoviesFromDb().asLiveDataListDomainMovies()

    fun getFavs() = savedState.getLiveData<List<DomainMovie>>("favsMovies")


        /* FILM & CREDITS */
    private var _film = MutableLiveData<DomainFilm?>()
//    val film: LiveData<DomainFilm?>
//        get() = _film
    fun getFilm() = savedState.getLiveData<DomainFilm?>("film")

    private var _person = MutableLiveData<DomainPerson?>()
    fun getPerson() = savedState.getLiveData<DomainPerson?>("person")



    init {
        if (savedState.keys().isNotEmpty()) {
//            Log.d(TAG,"savedState: ${savedState.keys()}")
            savedState.keys().forEach {
                when (it) {
                    "conf"          -> configuration = savedState.getLiveData("conf")
                    "popMovies"     -> _popularMoviesList = savedState.getLiveData("popMovies")
                    "foundMovies"   -> _foundMovies = savedState.getLiveData("foundMovies")
                    "favsMovies"    -> favsMovies = savedState.getLiveData("favsMovies")
                    "film"          -> _film = savedState.getLiveData("film")
                    "person"        -> _person = savedState.getLiveData("person")
                    // la extension de Hilt 'launchFragmentInHiltContainer crea esta key;
                    // para corregir la no inicializacion del viewModel en tests se hace necesario esto
                    "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY" -> {
                        downloadConfiguration()
                        downloadMovies()
                    }
                }
            }
        } else {
            _status.value = ApiStatus.DONE
            downloadConfiguration()
            downloadMovies()
        }
    }
    /**
     * Se restablecen las variables de paginación
     * de la busqueda a valor por defecto '1':
     * nextFoundPage
     * lastFoundPage
     */
    private fun resetSearchControls(){
        nextFoundPage = 1
        lastFoundPage = 1
    }

    /*
     * CONFIGURACION
     */
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
        configuration.value = repository.getConfiguration().images.asDomainModel()
        savedState["conf"] = configuration.value
    }

    fun feedImageUrl(field: String, size: Int = 2): String{
        val template = "%s%s/"
        val imageSize = when(field){
            "backdropSizes" -> getConf().value!!.backdropSizes[size]
            "logoSizes"     -> getConf().value!!.logoSizes[size]
            "posterSizes"   -> getConf().value!!.posterSizes[size]
            "profileSizes"  -> getConf().value!!.profileSizes[size]
            else            -> getConf().value!!.stillSizes[size]
        }
        return "${template.format(getConf().value?.secureBaseUrl, imageSize)}%s"
    }

    /*
     * POPULAR MOVIES
     */
    fun downloadMovies(){
        viewModelScope.launch {
            _status.value = ApiStatus.LOADING
            try {
                getPopularMovies()
            }catch (ce: CancellationException){
                throw ce
            }catch (e: Exception){
                _status.value = ApiStatus.ERROR
                // para no perder la página no descargada por el error
                nextPopPage--
                Toast.makeText(app, e.toString(), Toast.LENGTH_LONG).show()
                Log.d(TAG,"Error: $e")
            }
        }
    }
    private suspend fun getPopularMovies(){
        if (nextPopPage <= lastPopPage) {
            val moviesLIstDto = repository.downloadPopMovies(nextPopPage++)
            lastPopPage = moviesLIstDto.totalPages
            totalMovies = moviesLIstDto.totalResults

            if(!_popularMoviesList.isInitialized){
                _popularMoviesList.value = moviesLIstDto.results.asList()
                        .asListDomainMovies(
                            feedImageUrl("posterSizes")
                        )
//                Log.d(TAG,"getPopMovies: ${_popularMoviesList.value}")
            } else {
                _popularMoviesList.postValue(
                    _popularMoviesList.value!!.plus(
                        moviesLIstDto.results.asList()
                            .asListDomainMovies(
                                feedImageUrl("posterSizes")
                            )
                    )
                )
            }

            savedState["popMovies"] = _popularMoviesList.value
        } else {
            Toast.makeText(app, app.getText(R.string.noMas), Toast.LENGTH_LONG).show()
        }
        _status.value = ApiStatus.DONE
    }

    /*
     * FAVORITES MOVIES
     */
    fun renewFavsMovies() {
        savedState["favsMovies"] = favsMovies.value
    }

    fun saveFavMovie(movie: DomainMovie){
        viewModelScope.launch {
            guardarFav(movie)
        }
    }
    private suspend fun guardarFav(movie: DomainMovie){
        repository.insertFavMovie(movie.asMovieDb())
    }

    fun removeMovie(movieId: Int){
        viewModelScope.launch {
            repository.removeFavMovie(movieId)
        }
    }

    fun evalFavsInPops(parcial: Boolean) {
//        Log.d(TAG,"evaluando favoritos en populares: parcial $parcial")
        if (parcial) {
            val lastPos = getPops().value!!.size
            val rangoInf =  if (lastPos < 20)   0
            else                lastPos - 20

            getPops().value!!.subList(rangoInf, lastPos).forEach { pop ->
//                Log.d(TAG,"titulo en parcial: ${pop.title} -> ${pop.id}")
//                _favsMovies.value!!.forEach {
                getFavs().value!!.forEach{
                    if (it.id == pop.id) {
                        pop.fav = true
    //                        Log.d(TAG,"favorita: ${it.title}")
                    }
                }
            }
        } else {
            getPops().value!!.forEach { pop ->
//                _favsMovies.value!!.forEach {
                getFavs().value!!.forEach {
                    pop.fav = it.id == pop.id
                }
            }
        }
    }

    val noHayFavorites = savedState.getLiveData<List<DomainMovie>>("favsMovies").map {
        it.isEmpty()
    }

    /*
     * BUSQUEDA
     */
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

//         Log.d(TAG,"nextFoundPage: $nextFoundPage")
        if (nextFoundPage <= lastFoundPage) {
            val moviesListDto = repository.searchMovieFromRemote(query, nextFoundPage++)
            found = moviesListDto.totalResults
            lastFoundPage = moviesListDto.totalPages
            if( newQuery ) {
                _foundMovies.value = moviesListDto.results.asList()
                    .asListDomainMovies(
                        feedImageUrl("posterSizes")
                    )
                if (_foundMovies.value!!.isNotEmpty()) {
                    lastSuccessQuery = query
                    newQuery = false
                }
            } else {
                _foundMovies.value = _foundMovies.value!!.plus(
                    moviesListDto.results.asList().asListDomainMovies(
                        feedImageUrl("posterSizes")
                    )
                )
            }
            savedState["foundMovies"] = _foundMovies.value
        } else {
            Toast.makeText(app, app.getText(R.string.noMas), Toast.LENGTH_LONG).show()
//            Log.d(TAG,"busqueda sin resultados")
        }
        _status.value = ApiStatus.DONE
    }

    fun resetSearch(){
        _foundMovies.value = null
        savedState["foundMovies"] = null
        found = 0
    }

    val noFoundVisibility = _foundMovies.map {
        it?.isEmpty() ?: false
    }


    /*
     * FILM & CREDITS
     */
    fun getCredits(id: Int, title: String, overview: String){

        _status.value = ApiStatus.LOADING
        viewModelScope.launch {
            try {
                getMovieCredits(id, title, overview)
            }catch (ce: CancellationException){
                throw ce
            }catch (e: Exception){
                _status.value = ApiStatus.ERROR
                Toast.makeText(app, e.message, Toast.LENGTH_LONG).show()
                e.message?.let { Log.d(TAG, "getCredits: $it") }
            }
        }

        _status.value = ApiStatus.DONE
    }
    suspend fun getMovieCredits(id: Int, title: String, overview: String){
        val credits = repository.getMovieCredits(id)

        _film.value = DomainFilm(
            id,
            title,
            overview,
            credits.cast.asList().asListDomainModel(
                feedImageUrl("profileSizes", size = 1)
            ),
            credits.crew.asList().asListDomainModel()
        )
        savedState["film"] = _film.value
    }

    fun getPersonData(personId: Int){
        _status.value = ApiStatus.LOADING
        viewModelScope.launch {
            try {
                getPersonProfile(personId)
            }catch (ce: CancellationException){
                throw ce
            }catch (e: Exception){
                _status.value = ApiStatus.ERROR
                Toast.makeText(app, e.message, Toast.LENGTH_LONG).show()
//                e.message?.let { Log.d(TAG, "getCredits: $it") }
            }
        }
        _status.value = ApiStatus.DONE
    }
    private suspend fun getPersonProfile(personId: Int){
        _person.value = repository.getPerson(personId).asDomainModel(feedImageUrl("profileSizes"))
        savedState["person"] = _person.value
    }
    fun resetPerson(){
        _person.value = null
        savedState["person"] = null
    }

//    companion object{
//
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val savedStateHandle = createSavedStateHandle()
//                val application = this[APPLICATION_KEY] as WembleyMoviesApp
//                val moviesRepository = application.moviesRepository
//                MoviesViewModel(
//                    application,
//                    moviesRepository,
//                    savedStateHandle
//                )
//            }
//        }
//    }
}

