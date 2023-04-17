package com.gonpas.wembleymoviesapp.tabs

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gonpas.wembleymoviesapp.getOrAwaitValue
import com.gonpas.wembleymoviesapp.repository.FakeMoviesRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
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


    val testDispatcher = Dispatchers.Unconfined
    @Before
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @Before
    fun setupViewModel(){
        moviesRepository = FakeMoviesRepository()
        moviesViewModel = MoviesViewModel(ApplicationProvider.getApplicationContext(), moviesRepository)
    }

    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
    }


    @Test
    fun getConfigurationTest(){
       val value = moviesViewModel.configuration.getOrAwaitValue()

       assertThat(value, not(nullValue()))
    }
    @Test
    fun downloadMoviesTest() = runTest{
       val movies = moviesViewModel.popularMoviesList.getOrAwaitValue()

       assertThat(movies, (not(emptyList())))
       assertEquals(3, movies.size)
       assertEquals("El test de Navalosa", movies[2].title)
        // control de la construccion de la url de la imagen
       assertEquals("https://image.tmdb.org/t/p/w185/postername.jpg", movies[0].imgUrl)
    }

    @Test
    fun saveFavTest(){
        val movies = moviesViewModel.popularMoviesList.getOrAwaitValue()
        assertThat(movies.size, `is`(3))

        moviesViewModel.saveFavMovie(movies[0])
        val value = moviesViewModel.favsMovies.getOrAwaitValue()
        assertEquals(3, value.size)
    }


    @Test
    fun searchMoviesTest(){
        moviesViewModel.searchMovies("test")
        assertEquals(2, moviesViewModel.nextFoundPage)
        val value = moviesViewModel.foundMovies.getOrAwaitValue()
        assertEquals(2, value.size )
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
}