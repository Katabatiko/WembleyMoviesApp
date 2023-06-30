package com.gonpas.wembleymoviesapp.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gonpas.wembleymoviesapp.getOrAwaitValue
import com.gonpas.wembleymoviesapp.repository.FakeTestRepository
import com.gonpas.wembleymoviesapp.ui.tabs.MoviesViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class CrewTest{

    private lateinit var viewModel: MoviesViewModel

    @ExperimentalCoroutinesApi
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @Before
    fun setup() {
        val repository = FakeTestRepository()
        viewModel = MoviesViewModel(ApplicationProvider.getApplicationContext(), repository, SavedStateHandle())
    }

    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun getDirectorTest() = runTest{
        launch {
            viewModel.getCredits(640146, "Ant-Man y la Avispa", "overview")
            advanceUntilIdle()
            var domainFilm = viewModel.getFilm().getOrAwaitValue()

            var director = domainFilm?.getDirector() ?: "error"
            assertThat(director, `is`("Peyton Reed"))
            /////
            viewModel.getCredits(600, "Prueba", "overview")
            advanceUntilIdle()
            domainFilm = viewModel.getFilm().value

            director = domainFilm?.getDirector() ?: "error"
            assertThat(director, `is`("?"))
            /////
            viewModel.getCredits(100000, "Especial", "overview")
            advanceUntilIdle()
            domainFilm = viewModel.getFilm().value

            director = domainFilm?.getDirector() ?: "error"
            assertThat(director, `is`("Peyton Reed, Pedro Almodovar"))
        }
    }

    @Test
    fun getGuionistaTest() = runTest {
        launch {
            viewModel.getCredits(640146, "Ant-Man y la Avispa", "overview")
            advanceUntilIdle()
            var domainFilm = viewModel.getFilm().getOrAwaitValue()

            var guionista = domainFilm?.getGuionista() ?: "error"
            assertThat(guionista, `is`("?"))
            /////
            viewModel.getCredits(100002, "Prueba2", "overview")
            advanceUntilIdle()
            domainFilm = viewModel.getFilm().getOrAwaitValue()

            // guionistas como writer
            guionista = domainFilm?.getGuionista() ?: "error"
            assertThat(guionista, `is`("Jack Kirby, Stan Lee"))
            /////
            viewModel.getCredits(100000, "Especial", "overview")
            advanceUntilIdle()
            domainFilm = viewModel.getFilm().getOrAwaitValue()

            // guionistas como screenplay
            guionista = domainFilm?.getGuionista() ?: "error"
            assertThat(guionista, `is`("Stan Lee, Jack Kirby"))
        }
    }

    @Test
    fun getStoryByTest() = runTest {
        launch {
            viewModel.getCredits(640146, "Ant-Man y la Avispa", "overview")
            advanceUntilIdle()
            var domainFilm = viewModel.getFilm().getOrAwaitValue()

            var guionista = domainFilm?.getStoryBy() ?: "error"
            assertThat(guionista, `is`("?"))
            /////
            viewModel.getCredits(100002, "Prueba2", "overview")
            advanceUntilIdle()
            domainFilm = viewModel.getFilm().getOrAwaitValue()

            // guionistas como writer
            guionista = domainFilm?.getStoryBy() ?: "error"
            assertThat(guionista, `is`("Christophe Beck"))
            /////
            viewModel.getCredits(100000, "Especial", "overview")
            advanceUntilIdle()
            domainFilm = viewModel.getFilm().getOrAwaitValue()

            // guionistas como screenplay
            guionista = domainFilm?.getStoryBy() ?: "error"
            assertThat(guionista, `is`("Bill Pope"))
        }
    }
}