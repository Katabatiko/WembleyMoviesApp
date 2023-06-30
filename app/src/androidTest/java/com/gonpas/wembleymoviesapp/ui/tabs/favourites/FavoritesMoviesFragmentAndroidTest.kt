package com.gonpas.wembleymoviesapp.ui.tabs.favourites

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import com.gonpas.wembleymoviesapp.R
import com.gonpas.wembleymoviesapp.utils.MoviesAdapter
import com.gonpas.wembleymoviesapp.utils.clickItemChildWithId
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import androidx.test.filters.MediumTest
import com.gonpas.wembleymoviesapp.database.StubLocalData
import com.gonpas.wembleymoviesapp.repository.CustomRepositoryImpl
import com.gonpas.wembleymoviesapp.repository.MoviesRepository
import com.gonpas.wembleymoviesapp.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@MediumTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
//@RunWith(AndroidJUnit4::class) -> se ejecuta con el CustomTestRunner definido en el build.gradle, el cual hereda de AndroidJUnitRunner
class FavoritesMoviesFragmentAndroidTest {

    @Inject lateinit var repository: MoviesRepository
    private val testMovie = StubLocalData.movieDb1
    private val testMovie2 = StubLocalData.movieDb2
//    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)


    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        (repository as CustomRepositoryImpl).closeDb()
    }

    @Test
    fun onCreateViewTest() {
        launchFragmentInHiltContainer<FavoritesMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)
//        Thread.sleep(2000)

        onView(withId(R.id.list_fav_movies))
            .check(matches(isDisplayed()))

    }

    @Test
    fun clickOverview_showDialog() = runTest {
        repository.insertFavMovie(testMovie)

        launchFragmentInHiltContainer<FavoritesMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)
//        Thread.sleep(2000)

        onView(withId(R.id.list_fav_movies))
            .perform(
                RecyclerViewActions
                    .actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                        0,
                        clickItemChildWithId(R.id.overview)
                    )
            )
//        Thread.sleep(1000)

        onView(withText(android.R.string.ok))
            .inRoot(isDialog())
            .check(
                matches(isDisplayed())
            )
//        Thread.sleep(1000)
    }

    @Test
    fun clickDelFab_removeFav() = runTest {
        repository.insertFavMovie(testMovie)
        repository.insertFavMovie(testMovie2)

        launchFragmentInHiltContainer<FavoritesMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)

        onView(withId(R.id.list_fav_movies))
            .check(matches(hasDescendant(withText(testMovie.title))))

        // borrar una favorita
        onView(withId(R.id.list_fav_movies))
            .perform(
                RecyclerViewActions
                    .actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                        0,
                        clickItemChildWithId(R.id.floatingActionButton)
                    )
            )
//        Thread.sleep(1000)

        onView(withText(R.string.confirmar))
            .inRoot(isDialog())
            .check(
                matches(isDisplayed())
            )
//        Thread.sleep(1000)
        onView(withText(R.string.confirmar))
            .inRoot(isDialog())
            .perform(click())

        onView(withId(R.id.list_fav_movies))
            .check(matches(not(hasDescendant(withText(testMovie.title)))))
//        Thread.sleep(1000)
    }

    @Test
    fun noFavsVisualization() {
        launchFragmentInHiltContainer<FavoritesMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)

        Thread.sleep(500)

        onView(withId(R.id.aviso_no_hay)).check(matches(isDisplayed()))
    }
}