package com.gonpas.wembleymoviesapp.ui.tabs

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gonpas.wembleymoviesapp.getOrAwaitValue
import com.gonpas.wembleymoviesapp.repository.FakeTestRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
internal class MoviesViewModelTest{
    // viewmodel bajo pruebas
    private lateinit var moviesViewModel: MoviesViewModel
    // fake repository para ser inyectado en el viewmodel
    private lateinit var moviesRepository: FakeTestRepository


    // ejecuta cada tarea sincronamente usando Componentes de Arquitectura
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    val testDispatcher = StandardTestDispatcher()
    @Before
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @Before
    fun setupViewModel(){
        moviesRepository = FakeTestRepository()
        moviesViewModel = MoviesViewModel(ApplicationProvider.getApplicationContext(), moviesRepository, SavedStateHandle())
    }

    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
    }
    @After
    fun resetReturnError() {
        moviesRepository.shouldReturnError(false)
    }

    @Test
    fun feedImageUrlTest() = runTest {
        launch {

            var casiUrl = moviesViewModel.feedImageUrl("posterSizes")
            assertThat(casiUrl, `is`("https://image.tmdb.org/t/p/w185/%s"))

            casiUrl = moviesViewModel.feedImageUrl("logoSizes", 6)
            assertThat(casiUrl, `is`("https://image.tmdb.org/t/p/original/%s"))
        }
    }


    @Test
    fun getConfigurationTest() = runTest {
        launch {
            val value = moviesViewModel.getConf().getOrAwaitValue()

            assertThat(value, not(nullValue()))
        }
    }
    @Test
    fun downloadMoviesTest() = runTest {
        launch{
            val value = moviesViewModel.getConf().getOrAwaitValue()
            assertThat(value, not(nullValue()))

            val movies = moviesViewModel.getPops().getOrAwaitValue()

            assertThat(movies, `is`(not(emptyList())))
            assertEquals(4, movies.size)
            assertEquals("El test de Navalosa", movies[2].title)
            // control de la construccion de la url de la imagen
            assertEquals("https://image.tmdb.org/t/p/w185/postername.jpg", movies[0].imgUrl)
        }
    }

    @Test
    fun networkErrorTest() = runTest {
        moviesRepository.shouldReturnError(true)
        launch {
            // es necesario capturar el error que arroja la clase auxiliar para tests LiveDataTestUtil.kt
            try {
                moviesViewModel.getPops().getOrAwaitValue()
            }catch (e: TimeoutException){
                assertThat("LiveData value was never set.", `is`(e.message))
            }
            assertEquals(ApiStatus.ERROR, moviesViewModel.status.value)
        }
    }

    @Test
    fun getFavsMovies() = runTest {

        val favs = moviesViewModel.favsMovies.getOrAwaitValue()
        assertThat(favs, `is`(not(emptyList())))

        assertThat(favs.size.toString(), `is`("2"))
    }

    @Test
    fun saveFavTest() = runTest {
        launch {
            val movies = moviesViewModel.getPops().getOrAwaitValue()
            assertThat(movies.size, `is`(4))
            moviesViewModel.saveFavMovie(movies[0])
        }
//        advanceUntilIdle()
        launch {
            advanceUntilIdle()
            val value = moviesViewModel.favsMovies.getOrAwaitValue()
            assertEquals(3, value.size)
        }
    }

    @Test
    fun removeMovie() = runTest {
        launch {
            val value = moviesViewModel.favsMovies.getOrAwaitValue()
            assertThat(value[1].title, `is`("Vientos del norte"))

            moviesViewModel.removeMovie(100)
        }
//        advanceUntilIdle()
        launch {
            advanceUntilIdle()
            val newValue = moviesViewModel.favsMovies.getOrAwaitValue()
            assertThat(newValue.size.toString(), `is`("1"))
            assertThat(newValue[0].title, `is`("Vientos del norte"))
        }
    }


    @Test
    fun searchMoviesTest() = runTest {
        launch {
            moviesViewModel.searchMovies("test")
        }
        advanceUntilIdle()
        assertEquals(2, moviesViewModel.nextFoundPage)

        launch {
            val value = moviesViewModel.getFound().getOrAwaitValue()
            assertEquals(2, value?.size)
        }
    }

    @Test
    fun searchMovies_noFound() = runTest{
        launch {
            moviesViewModel.searchMovies("paráfrasis")
        }
        advanceUntilIdle()
        launch{
            val found = moviesViewModel.getFound().getOrAwaitValue()
            assertEquals(0, found?.size ?: -1)
        }
    }


    @Test
    fun getCredits() = runTest {
        launch {
            moviesViewModel.getCredits(
                640146,
                "Ant-Man and the Wasp: Quantumania",
                "La pareja de superhéroes Scott Lang y Hope van Dyne ..."
            )
        }
        advanceUntilIdle()
        launch {
            val film = moviesViewModel.getFilm().getOrAwaitValue()

            assertThat(film!!.title, `is`("Ant-Man and the Wasp: Quantumania"))
            assertThat(
                film.overview,
                `is`("La pareja de superhéroes Scott Lang y Hope van Dyne ...")
            )
            assertThat(film.cast[0].name, `is`("Paul Rudd"))
            assertThat(film.crew[0].job, `is`("Characters"))
            assertThat(film.crew[0].name, `is`("Stan Lee"))
        }

        launch {
            moviesViewModel.getCredits(22, "Null film", "Nada que contar")
        }
        advanceUntilIdle()
        launch {
            val film = moviesViewModel.getFilm().getOrAwaitValue()
            assertThat(film!!.title, `is`("Null film"))
            assertThat(film.overview, `is`("Nada que contar"))
            assertThat(film.cast.size, `is`(0))
            assertThat(film.crew.size, `is`(0))
        }
    }

    @Test
    fun getPersonData() = runTest {
        launch {
            moviesViewModel.getPersonData(19034)
        }
        advanceUntilIdle()

        launch {
            val person = moviesViewModel.getPerson().getOrAwaitValue()

            assertThat(person!!.name, `is`("Evangeline Lilly"))
            assertThat(person.placeOfBirth, `is`("Fort Saskatchewan, Alberta, Canada"))
        }

        launch {
            moviesViewModel.getPersonData(22)
        }
        advanceUntilIdle()
        launch {
            val person = moviesViewModel.getPerson().getOrAwaitValue()

            assertThat(person!!.name, `is`("Tedio Plomez Sopor"))
            assertThat(person.placeOfBirth, `is`("Madrid, España"))
        }
    }

}