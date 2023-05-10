package com.gonpas.wembleymoviesapp.ui.tabs

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gonpas.wembleymoviesapp.domain.DomainFilm
import com.gonpas.wembleymoviesapp.getOrAwaitValue
import com.gonpas.wembleymoviesapp.network.asListDomainModel
import com.gonpas.wembleymoviesapp.repository.FakeMoviesRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
internal class MoviesViewModelTest{
    // viewmodel bajo pruebas
    private lateinit var moviesViewModel: MoviesViewModel
    // fake repository para ser inyectado en el viewmodel
    private lateinit var moviesRepository: FakeMoviesRepository

    // necesario para testear livedata, permitiendo ejecutar codigo antes y despues del test
    // de manera sincrona y en orden repetible
    // establece el main coroutine dispatcher para los test unitarios
    /*@ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()*/

    // ejecuta cada tarea sincronamente usando Componentes de Arquitectura
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    @ExperimentalCoroutinesApi
    val testDispatcher = UnconfinedTestDispatcher()
    @Before
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @Before
    fun setupViewModel(){
        moviesRepository = FakeMoviesRepository()
        moviesViewModel = MoviesViewModel(ApplicationProvider.getApplicationContext(), moviesRepository, SavedStateHandle())
    }

    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
    }


    @Test
    fun getConfigurationTest(){
       val value = moviesViewModel._configuration.getOrAwaitValue()

       assertThat(value, not(nullValue()))
    }
    @Test
    fun downloadMoviesTest() {
        val value = moviesViewModel.getConf().getOrAwaitValue()
        assertThat(value, not(nullValue()))
        val movies = moviesViewModel.getPops().getOrAwaitValue()

        assertThat(movies, (not(emptyList())))
        assertEquals(4, movies.size)
        assertEquals("El test de Navalosa", movies[2].title)
        // control de la construccion de la url de la imagen
        assertEquals("https://image.tmdb.org/t/p/w185/postername.jpg", movies[0].imgUrl)
    }

    @Test
    fun saveFavTest(){
        val movies = moviesViewModel.getPops().getOrAwaitValue()
        assertThat(movies.size, `is`(4))

        moviesViewModel.saveFavMovie(movies[0])
        val value = moviesViewModel.favsMovies.getOrAwaitValue()
        assertEquals(3, value.size)
    }


    @Test
    fun searchMoviesTest(){
        moviesViewModel.searchMovies("test")
        assertEquals(2, moviesViewModel.nextFoundPage)
        val value = moviesViewModel.getFound().getOrAwaitValue()
        assertEquals(2, value?.size )
    }

    @Test
    fun getFavsMovies() {
        val value = moviesViewModel.favsMovies.getOrAwaitValue()

        assertThat(value.size.toString(), `is`("2"))
    }

    @Test
    fun removeMovie() {
        var value = moviesViewModel.favsMovies.getOrAwaitValue()
        assertThat(value[1].title, `is`("Vientos del norte"))

        moviesViewModel.removeMovie(100)

        value = moviesViewModel.favsMovies.getOrAwaitValue()
        assertThat(value.size.toString(), `is`("1"))
        assertThat(value[0].title, `is`("Vientos del norte"))

    }


    @Test
    fun getCredits(){
        moviesViewModel.getCredits(
            640146,
            "Ant-Man and the Wasp: Quantumania",
            "La pareja de superhéroes Scott Lang y Hope van Dyne regresa para continuar sus aventuras"
        )
        var film = moviesViewModel.getFilm().getOrAwaitValue()

        assertThat(film!!.title, `is`("Ant-Man and the Wasp: Quantumania"))
        assertThat(film.overview, `is`("La pareja de superhéroes Scott Lang y Hope van Dyne regresa para continuar sus aventuras"))
        assertThat(film.cast[0].name, `is`("Paul Rudd"))
        assertThat(film.crew[0].job, `is`("Characters"))
        assertThat(film.crew[0].name, `is`("Stan Lee"))

        moviesViewModel.getCredits(22, "Null film", "Nada que contar")
        film = moviesViewModel.getFilm().getOrAwaitValue()
        assertThat(film!!.title, `is`("Null film"))
        assertThat(film.overview, `is`("Nada que contar"))
        assertThat(film.cast.size, `is`(0))
        assertThat(film.crew.size, `is`(0))
    }

    @Test
    fun getPersonData(){
        moviesViewModel.getPersonData(19034)
        var person = moviesViewModel.getPerson().getOrAwaitValue()

        assertThat(person!!.name, `is`("Evangeline Lilly"))
        assertThat(person.placeOfBirth, `is`("Fort Saskatchewan, Alberta, Canada"))

        moviesViewModel.getPersonData(22)
        person = moviesViewModel.getPerson().getOrAwaitValue()

        assertThat(person!!.name, `is`("Tedio Plomez Sopor"))
        assertThat(person.placeOfBirth, `is`("Madrid, España"))
    }

}