package com.gonpas.wembleymoviesapp.tabs.favourites

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gonpas.wembleymoviesapp.MainCoroutineRule
import com.gonpas.wembleymoviesapp.database.MovieDb
import com.gonpas.wembleymoviesapp.domain.DomainMovie
import com.gonpas.wembleymoviesapp.getOrAwaitValue
import com.gonpas.wembleymoviesapp.repository.FakeMoviesRepository
import com.gonpas.wembleymoviesapp.repository.movieDb1
import kotlinx.coroutines.Dispatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class FavoritesMoviesViewModelTest {
    // viewmodel bajo pruebas
    private lateinit var favMoviesViewModel: FavoritesMoviesViewModel
    // Use a fake repository to be injected into the viewmodel
    private lateinit var moviesRepository: FakeMoviesRepository

    private lateinit var observer: Observer<List<DomainMovie>>

    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()


    // necesario para testear livedata, permitiendo ejecutar codigo antes y despues del test
    // de manera sincrona y en orden repetible
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        moviesRepository = FakeMoviesRepository()
        favMoviesViewModel = FavoritesMoviesViewModel(ApplicationProvider.getApplicationContext(), moviesRepository)
    }

    @Before
    fun createObserver(){
         observer = Observer {}
    }

    @Before
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Test
    fun getFavsMovies() {
//            favMoviesViewModel.favsMovies.getOrAwaitValue()
            val value = favMoviesViewModel.favsMovies.getOrAwaitValue()

//            assertEquals(2, favMoviesViewModel.favsMovies.value!!.size)
            assertThat(value.size.toString(), `is`("2"))
    }

    @Test
    fun removeMovie() = runTest {
            /*val value = favMoviesViewModel.favsMovies.getOrAwaitValue()
//            val repoMovies = moviesRepository.getMoviesFromDb().getOrAwaitValue()

            favMoviesViewModel.removeMovie(movieDb1.movieId)

//            assertEquals(1, repoMovies?.size ?: 100)
            assertThat(value[1].title, `is`("Vientos del norte"))
            assertThat(value.size.toString(), `is`("1"))
            assertThat(value[0].title, `is`("Vientos del norte"))*/

        val observer = Observer<List<DomainMovie>>{}
        try {
            favMoviesViewModel.favsMovies.observeForever(observer)
            favMoviesViewModel.removeMovie(movieDb1.movieId)

            val value = favMoviesViewModel.favsMovies.value

            assertThat(value!!.size.toString(), `is`("1"))
            assertThat(value[0].title, `is`("Vientos del norte"))
        } finally {
            favMoviesViewModel.favsMovies.removeObserver(observer)
        }
    }
}