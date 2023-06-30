package com.gonpas.wembleymoviesapp.ui.tabs.movie

import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.gonpas.wembleymoviesapp.R
import com.gonpas.wembleymoviesapp.domain.DomainFilm
import com.gonpas.wembleymoviesapp.domain.DomainMovie
import com.gonpas.wembleymoviesapp.network.StubRemoteService.movieDto4
import com.gonpas.wembleymoviesapp.network.asListDomainModel
import com.gonpas.wembleymoviesapp.network.asListDomainMovies
import com.gonpas.wembleymoviesapp.utils.MoviesAdapter
import com.gonpas.wembleymoviesapp.utils.clickItemChildWithId
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import androidx.test.filters.MediumTest
import com.gonpas.wembleymoviesapp.repository.MoviesRepository
import com.gonpas.wembleymoviesapp.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.launch
import org.junit.Test

import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import javax.inject.Inject


@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
//@RunWith(AndroidJUnit4::class) -> se ejecuta con el CustomTestRunner definido en el build.gradle, el cual hereda hereda de AndroidJUnitRunner
class MovieFragmentAndroidTest {

    @Inject lateinit var repository: MoviesRepository
    private lateinit var domainMovie: DomainMovie

    val casiUrl = "http://image.tmdb.org/t/p/w185/%s"

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup(){
        hiltRule.inject()
        domainMovie = listOf(movieDto4).asListDomainMovies(casiUrl)[0]
    }


    @Test
    fun onCreateView() = runTest{
        launch {
            val credits = repository.getMovieCredits(domainMovie.id)
            val film = DomainFilm(
                domainMovie.id,
                domainMovie.title,
                domainMovie.overview,
                credits.cast.asList().asListDomainModel(casiUrl),
                credits.crew.asList().asListDomainModel()
                )
            val bundle = bundleOf("film" to film)

            launchFragmentInHiltContainer<MovieFragment>(bundle, R.style.Theme_WembleyMoviesApp)

            onView(withId(R.id.movieFragment))
                .check(matches(isDisplayed()))
//            Thread.sleep(2000)
        }
    }

    @Test
    fun clickOnCastItem_launchPersonDialog() = runTest {
        launch {
            val credits = repository.getMovieCredits(domainMovie.id)

            val activeFilm = DomainFilm(
                domainMovie.id,
                domainMovie.title,
                domainMovie.overview,
                credits.cast.asList().asListDomainModel(casiUrl),
                credits.crew.asList().asListDomainModel()
            )
            val bundle = bundleOf("film" to activeFilm)
            launchFragmentInHiltContainer<MovieFragment>(
                bundle,
                R.style.Theme_WembleyMoviesApp
            )
//            Thread.sleep(2000)

            // pulsar en biografía del actor posicion 1
            onView(withId(R.id.casting))
                .perform(
                    RecyclerViewActions
                        .actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                            1,
                            clickItemChildWithId(R.id.biografia)
                        )
                )
//            Thread.sleep(2000)

            onView(withId(R.id.textView11))
                .check(matches(withText("https://evangeline-lilly.com/")))

            onView(withId(R.id.ok_button))
                .inRoot(RootMatchers.isDialog())
                .check(
                    matches(isDisplayed())
                )
                .perform(click())

            onView(withId(R.id.casting))
                .perform(
                    RecyclerViewActions
                        .actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                            0,
                            clickItemChildWithId(R.id.biografia)
                        )
                )
//            Thread.sleep(3000)

            onView(withId(R.id.ok_button))
                .inRoot(RootMatchers.isDialog())
                .check(
                    matches(isDisplayed())
                )
                .perform(click())
        }
    }
}