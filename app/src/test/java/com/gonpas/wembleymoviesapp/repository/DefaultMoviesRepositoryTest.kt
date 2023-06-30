package com.gonpas.wembleymoviesapp.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.gonpas.wembleymoviesapp.database.FakeLocalDataSource
import com.gonpas.wembleymoviesapp.domain.asMovieDb
import com.gonpas.wembleymoviesapp.network.FakeRemoteService
import com.gonpas.wembleymoviesapp.network.StubTestRemoteService
import com.gonpas.wembleymoviesapp.network.asListDomainMovies
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DefaultMoviesRepositoryTest {

    private lateinit var repository: MoviesRepository

    // ejecuta cada tarea sincronamente usando Componentes de Arquitectura
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        repository = DefaultMoviesRepository(FakeRemoteService, FakeLocalDataSource(), testDispatcher )
    }

    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun downloadPopMovies() = runTest {
        launch {
            val movies = repository.downloadPopMovies(1)

            assertArrayEquals(movies.results, StubTestRemoteService.actualMoviesDto.results)
        }
    }

    @Test
    fun searchMovie() = runTest {
        launch {
            val initValue = repository.downloadPopMovies(1).results
            assertThat(initValue.size.toString(), `is`("4"))
        }
        launch {
            val value = repository.searchMovieFromRemote("norte",1).results
            assertThat("1", `is`( value.size.toString()))
            assertThat("Vientos del norte", `is`(value[0].title))
        }
    }

    @Test
    fun getConfiguration() = runTest{
        val conf = repository.getConfiguration()
        assertThat(conf.images.baseUrl, `is`("http://image.tmdb.org/t/p/"))
    }

    @Test
    fun getMoviesFromDb() = runTest{
        val movies = repository.downloadPopMovies(1).results
        assertThat("4", `is`( movies.size.toString()))
    }

    @Test
    fun insertFavMovie() = runTest(testDispatcher.scheduler){

        val casiUrl = "http://image.tmdb.org/t/p/w185/%s"

        val movies = repository.downloadPopMovies(1)
            .results.asList().asListDomainMovies(casiUrl)

        repository.insertFavMovie(movies[0].asMovieDb())

        val value = repository.getMoviesFromDb().value
        TestCase.assertEquals(3, value?.size ?: 9)
    }

    @Test
    fun removeFavMovie() = runTest(testDispatcher.scheduler) {
        repository.removeFavMovie(100)
        val value = repository.getMoviesFromDb().value
        TestCase.assertEquals(1, value?.size ?: 9)
    }

    @Test
    fun getMovieCredits() = runTest {
        launch {
            var credits = repository.getMovieCredits(640146)
            assertThat(credits.cast[0].name, `is`("Paul Rudd"))

            val director = credits.crew.filter {
                it.job == "Director"
            }[0]
            assertThat(director.name, `is`("Peyton Reed"))

            credits = repository.getMovieCredits(10)
            assertThat(credits.crew.size, `is`(0))
            assertThat(credits.cast.size, `is`(0))
        }
    }

    @Test
    fun getPerson() = runTest {
        launch{
            var person = repository.getPerson(19034)
            assertThat(person.name, `is`("Evangeline Lilly"))
            assertThat(person.placeOfBirth, `is`("Fort Saskatchewan, Alberta, Canada"))

            person = repository.getPerson(111)
            assertThat(person.name, `is`("Tedio Plomez Sopor"))
            assertThat(person.popularity, `is`(99.99f))
        }
    }
}