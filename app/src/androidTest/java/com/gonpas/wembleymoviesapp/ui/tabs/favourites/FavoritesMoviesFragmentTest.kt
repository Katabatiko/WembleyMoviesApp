package com.gonpas.wembleymoviesapp.ui.tabs.favourites

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gonpas.wembleymoviesapp.R
import com.gonpas.wembleymoviesapp.ServiceLocator
import com.gonpas.wembleymoviesapp.database.FakeAndroidTestLocalDataSource
import com.gonpas.wembleymoviesapp.repository.FakeAndroidTestRepository
import com.gonpas.wembleymoviesapp.utils.MoviesAdapter
import com.gonpas.wembleymoviesapp.utils.clickItemChildWithId
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import androidx.test.filters.MediumTest
import org.hamcrest.Matchers.not

import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class FavoritesMoviesFragmentTest {

    private lateinit var repository: FakeAndroidTestRepository
    private val testMovie = FakeAndroidTestLocalDataSource().movieDb1

    @Before
    fun initRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.repository = repository
    }

    @After
    fun cleanupDb() = runTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun onCreateViewTest() {
        launchFragmentInContainer<FavoritesMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)
        onView(withId(R.id.list_fav_movies))
            .check(matches(isDisplayed()))

        Thread.sleep(2000)
    }

    @Test
    fun clickOverview_showDialog() {
        launchFragmentInContainer<FavoritesMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)

        onView(withId(R.id.list_fav_movies))
            .perform(
                RecyclerViewActions
                    .actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                        0,
                        clickItemChildWithId(R.id.overview)
                    )
            )
        Thread.sleep(1000)
        // se busca el texto del boton aceptar, porque el dialogo no pertenece a la jerarquia normal
        onView(withText(android.R.string.ok))
            .inRoot(isDialog())
            .check(
                matches(isDisplayed())
            )
        Thread.sleep(1000)
    }

    @Test
    fun clickDelFab_removeFav() {
        launchFragmentInContainer<FavoritesMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)


        onView(withId(R.id.list_fav_movies))
            .check(matches(hasDescendant(withText(testMovie.title))))

        onView(withId(R.id.list_fav_movies))
            .perform(
                RecyclerViewActions
                    .actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                        0,
                        clickItemChildWithId(R.id.floatingActionButton)
                    )
            )
        Thread.sleep(1000)
        // se busca el texto del boton confirmar, porque el dialogo no pertenece a la jerarquia normal
        onView(withText(R.string.confirmar))
            .inRoot(isDialog())
            .check(
                matches(isDisplayed())
            )
        Thread.sleep(1000)
        onView(withText(R.string.confirmar))
            .perform(
                click()
            )

        onView(withId(R.id.list_fav_movies))
            .check(matches(not(hasDescendant(withText(testMovie.title)))))
        Thread.sleep(1000)
    }

    @Test
    fun noFavsVisualization() {
        launchFragmentInContainer<FavoritesMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)

        /*onView(withId(R.id.aviso_no_hay))
            .check(matches(isDisplayed()))*/

        onView(withId(R.id.list_fav_movies))
            .perform(
                RecyclerViewActions
                    .actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                        0,
                        clickItemChildWithId(R.id.floatingActionButton)
                    )
            )
        onView(withText(R.string.confirmar))
            .perform(
                click()
            )
        Thread.sleep(500)
        onView(withId(R.id.list_fav_movies))
            .perform(
                RecyclerViewActions
                    .actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                        0,
                        clickItemChildWithId(R.id.floatingActionButton)
                    )
            )
        onView(withText(R.string.confirmar))
            .perform(
                click()
            )

        Thread.sleep(500)
        onView(withId(R.id.list_fav_movies))
            .perform(
                RecyclerViewActions
                    .actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                        0,
                        clickItemChildWithId(R.id.floatingActionButton)
                    )
            )
        onView(withText(R.string.confirmar))
            .perform(
                click()
            )


        onView(withId(R.id.aviso_no_hay))
            .check(matches(isDisplayed()))

        Thread.sleep(1000)
    }
}