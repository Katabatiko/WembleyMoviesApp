package com.gonpas.wembleymoviesapp.ui.tabs.popular

import com.gonpas.wembleymoviesapp.R
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.gonpas.wembleymoviesapp.ServiceLocator
import com.gonpas.wembleymoviesapp.WembleyMoviesApp
import com.gonpas.wembleymoviesapp.network.FakeAndroidTestRemoteService.movieDto1
import com.gonpas.wembleymoviesapp.repository.FakeAndroidTestRepository
import com.gonpas.wembleymoviesapp.ui.tabs.popular.PopularMoviesFragment.*
import com.gonpas.wembleymoviesapp.utils.ItemChildViewVisibilityAssertion.Companion.isItemChildViewNotVisible
import com.gonpas.wembleymoviesapp.utils.ItemChildViewVisibilityAssertion.Companion.isItemChildViewVisible
import com.gonpas.wembleymoviesapp.utils.ItemChildTextViewCorrectText.Companion.isTextItemChildCorrect
import com.gonpas.wembleymoviesapp.utils.MoviesAdapter
import com.gonpas.wembleymoviesapp.utils.clickItemChildWithId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith

@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PopularMoviesFragmentTest {

    lateinit var repository: FakeAndroidTestRepository
    val testMovie = movieDto1

    @Before
    fun initRepository(){
        repository = FakeAndroidTestRepository()
        ServiceLocator.repository = repository
    }

    @After
    fun cleanupDb() = runTest {
        ServiceLocator.resetRepository()
    }

    @Test
    fun onCreateViewTest()  {
        launchFragmentInContainer<PopularMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)
        onView(withId(R.id.list_popular_movies)).check(matches(isDisplayed()))
        Thread.sleep(2000)
    }

    @Test
    fun viewTextHasCorrectText(){
        launchFragmentInContainer<PopularMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)

        Thread.sleep(1000)
        val voteAverageTemplateFormated = getApplicationContext<WembleyMoviesApp>()
            .resources.getString(R.string.calificacion)
            .format(testMovie.voteAverage.toString().replace('.',','))

        val date = testMovie.releaseDate.split("-")
        val formatedDate = getApplicationContext<WembleyMoviesApp>()
            .resources.getString(R.string.fecha_estreno)
            .format(date[2],date[1],date[0])

        onView(withId(R.id.list_popular_movies))
            .check(
                isTextItemChildCorrect(R.id.titulo,testMovie.title)
            )
            .check(
                isTextItemChildCorrect(R.id.overview, testMovie.overview)
            )
            .check(
                isTextItemChildCorrect(R.id.voteAverageTv, voteAverageTemplateFormated)
            )
            .check(
                isTextItemChildCorrect(R.id.releaseDate, formatedDate)
            )

        Thread.sleep(1000)
    }

    @Test
    fun isSaveFabVisible() {
        launchFragmentInContainer<PopularMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)

        Thread.sleep(1000)
        onView(withId(R.id.list_popular_movies))
            .check(
                isItemChildViewVisible(R.id.floatingActionButton)
            )
        Thread.sleep(1000)
    }

    @Test
    fun saveMovie_HideSaveFab() {
        launchFragmentInContainer<PopularMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)

        Thread.sleep(1000)
        onView(withId(R.id.list_popular_movies))
            .check(
                isItemChildViewVisible(R.id.floatingActionButton)
            )
            .perform(
                RecyclerViewActions
                    .actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(0,
                        clickItemChildWithId(R.id.floatingActionButton)
                    )
            )
            .check(
                isItemChildViewNotVisible(R.id.floatingActionButton)
            )

        Thread.sleep(1000)
    }

    @Test
    fun clickOverview_showDialog(){
        launchFragmentInContainer<PopularMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)

        onView(withId(R.id.list_popular_movies))
            .perform(
                RecyclerViewActions
                    .actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(0,
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
}