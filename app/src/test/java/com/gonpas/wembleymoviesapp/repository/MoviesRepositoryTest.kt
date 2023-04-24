package com.gonpas.wembleymoviesapp.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gonpas.wembleymoviesapp.database.FakeLocalDataSource
import com.gonpas.wembleymoviesapp.domain.asMovieDb
import com.gonpas.wembleymoviesapp.network.FakeRemoteService
import com.gonpas.wembleymoviesapp.network.asListDomainMovies
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MoviesRepositoryTest {

    private lateinit var repository: MoviesRepository

    // ejecuta cada tarea sincronamente usando Componentes de Arquitectura
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    val testDispatcher = UnconfinedTestDispatcher()
    @Before
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @Before
    fun setUp() {
        val testDispatcher = UnconfinedTestDispatcher()
        repository = MoviesRepository(FakeRemoteService, FakeLocalDataSource(), testDispatcher )
    }

    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    fun downloadPopMovies() = runTest {
        withContext(testDispatcher) {
            val movies = repository.downloadPopMovies(1)

            assertArrayEquals(movies.results, FakeRemoteService.actualMoviesDto.results)
        }
    }

    @Test
    fun searchMovie() = runTest {
        withContext(testDispatcher) {
            val initValue = repository.downloadPopMovies(1).results
            assertThat("3", `is`( initValue.size.toString()))
            val value = repository.searchMovieFromRemote("norte",1).results
            assertThat("1", `is`( value.size.toString()))
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
        assertThat("3", `is`( movies.size.toString()))
    }

    @Test
    fun insertFavMovie() = runTest{
        val movies = repository.downloadPopMovies(1).results.asList()
            .asListDomainMovies("http://image.tmdb.org/t/p/","w185")
        repository.insertFavMovie(movies[0].asMovieDb())
        val value = repository.getMoviesFromDb().value
        TestCase.assertEquals(3, value?.size ?: 9)
    }

    @Test
    fun removeFavMovie() = runTest{
        repository.removeFavMovie(100)
        val value = repository.getMoviesFromDb().value
        TestCase.assertEquals(1, value?.size ?: 9)
    }
}