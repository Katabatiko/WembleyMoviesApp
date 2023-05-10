package com.gonpas.wembleymoviesapp.ui.tabs.movie

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import com.gonpas.wembleymoviesapp.R
import com.gonpas.wembleymoviesapp.ServiceLocator
import com.gonpas.wembleymoviesapp.domain.DomainFilm
import com.gonpas.wembleymoviesapp.domain.DomainMovie
import com.gonpas.wembleymoviesapp.network.FakeAndroidTestRemoteService.movieDto3
import com.gonpas.wembleymoviesapp.network.FakeAndroidTestRemoteService.movieDto4
import com.gonpas.wembleymoviesapp.network.asListDomainModel
import com.gonpas.wembleymoviesapp.network.asListDomainMovies
import com.gonpas.wembleymoviesapp.repository.FakeAndroidTestRepository
import com.gonpas.wembleymoviesapp.utils.MoviesAdapter
import com.gonpas.wembleymoviesapp.utils.casiUrl
import com.gonpas.wembleymoviesapp.utils.clickItemChildWithId
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import androidx.test.filters.MediumTest
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before



@MediumTest
@ExperimentalCoroutinesApi
class MovieFragmentTest {
    private lateinit var repository: FakeAndroidTestRepository
    private lateinit var domainMovie: DomainMovie

    @Before
    fun initRepository(){
        repository = FakeAndroidTestRepository()
        ServiceLocator.repository = repository
        domainMovie = listOf(movieDto4).asListDomainMovies(casiUrl)[0]
    }

    @After
    fun cleanupDb() = runTest {
        ServiceLocator.resetRepository()
    }


    @Test
    fun onCreateView() = runTest{
        withContext(UnconfinedTestDispatcher()) {
            val credits = repository.getMovieCredits(domainMovie.id)
            val film = DomainFilm(
                domainMovie.id,
                domainMovie.title,
                domainMovie.overview,
                credits.cast.asList().asListDomainModel(casiUrl),
                credits.crew.asList().asListDomainModel()
                )
            val bundle = bundleOf("film" to film)
            launchFragmentInContainer<MovieFragment>(bundle, R.style.Theme_WembleyMoviesApp)
            onView(ViewMatchers.withId(R.id.movieFragment))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Thread.sleep(2000)
        }
    }

    @Test
    fun clickOnCastItem_launchPersonDialog() = runTest {
        withContext(UnconfinedTestDispatcher()) {
            var credits = repository.getMovieCredits(domainMovie.id)

            var activeFilm = DomainFilm(
                domainMovie.id,
                domainMovie.title,
                domainMovie.overview,
                credits.cast.asList().asListDomainModel(casiUrl),
                credits.crew.asList().asListDomainModel()
            )
            var bundle = bundleOf("film" to activeFilm)
            /*val scenario = */launchFragmentInContainer<MovieFragment>(
                bundle,
                R.style.Theme_WembleyMoviesApp
            )
            Thread.sleep(2000)

            onView(ViewMatchers.withId(R.id.casting))
                .perform(
                    RecyclerViewActions
                        .actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                            1,
                            clickItemChildWithId(R.id.biografia)
                        )
                )

            onView(ViewMatchers.withId(R.id.textView11))
                .check(ViewAssertions.matches(ViewMatchers.withText("https://evangeline-lilly.com/")))
            Thread.sleep(2000)

            onView(ViewMatchers.withId(R.id.ok_button))
                .inRoot(RootMatchers.isDialog())
                .check(
                    ViewAssertions.matches(ViewMatchers.isDisplayed())
                )
                .perform(click())

            onView(ViewMatchers.withId(R.id.casting))
                .perform(
                    RecyclerViewActions
                        .actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                            0,
                            clickItemChildWithId(R.id.biografia)
                        )
                )
            Thread.sleep(3000)

            domainMovie = listOf(movieDto3).asListDomainMovies(casiUrl)[0]
            credits = repository.getMovieCredits(domainMovie.id)

            activeFilm = DomainFilm(
                domainMovie.id,
                domainMovie.title,
                domainMovie.overview,
                credits.cast.asList().asListDomainModel(casiUrl),
                credits.crew.asList().asListDomainModel()
            )

            bundle = bundleOf("film" to activeFilm)
            launchFragmentInContainer<MovieFragment>(
                bundle,
                R.style.Theme_WembleyMoviesApp
            )
            Thread.sleep(2000)
        }
    }
}