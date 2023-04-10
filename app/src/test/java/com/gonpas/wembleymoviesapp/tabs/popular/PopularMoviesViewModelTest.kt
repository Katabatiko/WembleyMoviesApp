package com.gonpas.wembleymoviesapp.tabs.popular

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gonpas.wembleymoviesapp.repository.FakeMoviesRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
internal class PopularMoviesViewModelTest{
    // viewmodel bajo pruebas
    private lateinit var popularMoviesViewModel: PopularMoviesViewModel
    // Use a fake repository to be injected into the viewmodel
    private lateinit var moviesRepository: FakeMoviesRepository


    // necesario para testear livedata, permitiendo ejecutar codigo antes y despues del test
    // de manera sincrona y en orden repetible
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        moviesRepository = FakeMoviesRepository()
        popularMoviesViewModel = PopularMoviesViewModel(ApplicationProvider.getApplicationContext(), moviesRepository)
    }

    @Test
    fun downloadInitialDataTest(){
        setupViewModel()
        assertEquals(
            3, popularMoviesViewModel.popularMoviesList.value!!.size
        )
        assertEquals(
            "El test de Navalosa", popularMoviesViewModel.popularMoviesList.value!![2].title
        )
        assertEquals(
            "https://image.tmdb.org/t/p/w185/postername.jpg", popularMoviesViewModel.popularMoviesList.value!![0].imgUrl
        )
    }

    @Test
    fun saveFavTest(){
        assertEquals(2, moviesRepository.getMoviesFromDb().value!!.size)
        popularMoviesViewModel.saveFavMovie(popularMoviesViewModel.popularMoviesList.value!![0])
        assertEquals(3, moviesRepository.getMoviesFromDb().value!!.size)
    }


    @Test
    fun searchMoviesTest(){
        popularMoviesViewModel.searchMovies("test")
//        assertEquals(2, popularMoviesViewModel.nextFoundPage)
        assertEquals(2, popularMoviesViewModel.foundMovies.value?.size ?: 5)
    }

}