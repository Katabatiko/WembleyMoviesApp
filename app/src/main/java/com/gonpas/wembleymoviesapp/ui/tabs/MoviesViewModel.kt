package com.gonpas.wembleymoviesapp.ui.tabs

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.gonpas.wembleymoviesapp.R
import com.gonpas.wembleymoviesapp.database.asListDomainMovies
import com.gonpas.wembleymoviesapp.domain.*
import com.gonpas.wembleymoviesapp.network.*
import com.gonpas.wembleymoviesapp.repository.InterfaceMoviesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

enum class ApiStatus { LOADING, ERROR, DONE }

private const val TAG = "xxMvm"


class MoviesViewModel(
    val app: Application,
    private val repository: InterfaceMoviesRepository,
    savedState: SavedStateHandle
    ) : AndroidViewModel(app) {

    //SavedStateHandle
    val state = savedState


    // LiveDatas
    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status
    fun resetStatus(){
        _status.value = ApiStatus.DONE
    }

    private val _popularMoviesList = MutableLiveData<List<DomainMovie>>()
//    val popularMoviesList: LiveData<List<DomainMovie>>
//        get() = _popularMoviesList
    fun getPops() = state.getLiveData<List<DomainMovie>>("popMovies")
    var totalMovies = 0
    private var nextPopPage = 1
    private var lastPopPage = 1

    private val _foundMovies = MutableLiveData<List<DomainMovie>?>()
//    val foundMovies: LiveData<List<DomainMovie>?>
//        get() = _foundMovies
    fun getFound() = state.getLiveData<List<DomainMovie>?>("foundMovies")
    var found = 0
    var nextFoundPage = 1
    private var lastFoundPage = 1
    private var lastSuccessQuery = ""

    val _configuration = MutableLiveData<DomainImages> ()
//    val configuration: LiveData<DomainImages>
//        get() = _configuration
    fun getConf() = state.getLiveData<DomainImages>("conf")



    /* FAVORITES */
    val favsMovies: LiveData<List<DomainMovie>> = repository.getMoviesFromDb().asListDomainMovies()
//    private val _favsMovies = state.getLiveData<List<DomainMovie>>("favsMovies")
//    val favsMovies: LiveData<List<DomainMovie>>
//        get() = _favsMovies
//    fun getFavs() = state.getLiveData<List<DomainMovie>>("favsMovies")


        /* FILM & CREDITS */
    private val _film = MutableLiveData<DomainFilm?>()
//    val film: LiveData<DomainFilm?>
//        get() = _film
    fun getFilm() = state.getLiveData<DomainFilm?>("film")

    private val _person = MutableLiveData<DomainPerson?>()
//    val person: LiveData<DomainPerson?>
//        get() = _person
    fun getPerson() = state.getLiveData<DomainPerson?>("person")



    init {
//        Log.d(TAG,"savedState: ${savedState.keys()}")
//        getFavoritesMovies()
        downloadConfiguration()
        downloadMovies()
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

    /**
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
        _configuration.value = repository.getConfiguration().images.asDomainModel()
        state["conf"] = _configuration.value
    }

    fun feedImageUrl(field: String, size: Int = 2): String{
        val template = "%s%s/"
        val imageSize = when(field){
            "backdropSizes" -> getConf().value!!.backdropSizes.get(size)
            "logoSizes"     -> getConf().value!!.logoSizes.get(size)
            "posterSizes"   -> getConf().value!!.posterSizes.get(size)
            "profileSizes"  -> getConf().value!!.profileSizes.get(size)
            else            -> getConf().value!!.stillSizes.get(size)
        }
        return "${template.format(getConf().value?.secureBaseUrl, imageSize)}%s"
    }

    /**
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
                Toast.makeText(app, e.message, Toast.LENGTH_LONG).show()
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

            state["popMovies"] = _popularMoviesList.value
        } else {
            Toast.makeText(app, app.getText(R.string.noMas), Toast.LENGTH_LONG).show()
        }
        _status.value = ApiStatus.DONE
    }

    /**
     * FAVORITES MOVIES
     */
    private fun getFavoritesMovies(){
        viewModelScope.launch {
            try {
                retrieveFavorites()
//                state["favsMovies"] = repository.getMoviesFromDb().asListDomainMovies().value
            }catch (ce: CancellationException){
                throw ce
            }catch (e: Exception){
                _status.value = ApiStatus.ERROR
                Log.d(TAG,"onError downloading favsMovies: $e")
                Toast.makeText(app, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun retrieveFavorites(){
        val favsMovies = repository.getMoviesFromDb().asListDomainMovies()
        Log.d(TAG,"retrieveFavorites: ${repository.getMoviesFromDb().asListDomainMovies().value}")
        state["favsMovies"] = favsMovies.value
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
                favsMovies.value!!.forEach {
//                getFavs().value!!.forEach{
                    if (it.id == pop.id) {
                        pop.fav = true
    //                        Log.d(TAG,"favorita: ${it.title}")
                    }
                }
            }
        } else {
            getPops().value!!.forEach { pop ->
//                Log.d(TAG,"pop.title: ${pop.title} -> ${pop.id}")
                favsMovies.value!!.forEach {
//                getFavs().value!!.forEach {
                    pop.fav = it.id == pop.id
//                    Log.d(TAG,"fav.id: ${it.id} - pop.fav: ${pop.fav}")

                }
            }
        }
    }

    val noHayFavorites = favsMovies.map {
        it.isEmpty()
    }

    /**
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
            state["foundMovies"] = _foundMovies.value
        } else {
            Toast.makeText(app, app.getText(R.string.noMas), Toast.LENGTH_LONG).show()
//            Log.d(TAG,"busqueda sin resultados")
        }
        _status.value = ApiStatus.DONE
    }

    fun resetSearch(){
        _foundMovies.value = null
        state["foundMovies"] = null
        found = 0
    }

    val noFoundVisibility = _foundMovies.map {
        it?.isEmpty() ?: false
    }


    /**
     * FILM & CREDITS
     */
    fun getCredits(id: Int, title: String, overview: String){

        _status.value = ApiStatus.LOADING
        Log.d(TAG,"status: ${status.value}")
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
        state["film"] = _film.value
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
    suspend fun getPersonProfile(personId: Int){
        _person.value = repository.getPerson(personId).asDomainModel(feedImageUrl("profileSizes"))
        state["person"] = _person.value
    }
    fun resetPerson(){
        _person.value = null
        state["person"] = null
    }
}


@Suppress("UNCHECKED_CAST")
class MoviesViewModelFactory(
    private val app: Application,
    private val moviesRepository: InterfaceMoviesRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
    ): AbstractSavedStateViewModelFactory(owner, defaultArgs){


    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return MoviesViewModel(app, moviesRepository, handle) as T
    }
}