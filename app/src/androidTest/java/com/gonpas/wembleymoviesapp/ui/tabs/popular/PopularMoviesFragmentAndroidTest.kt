package com.gonpas.wembleymoviesapp.ui.tabs.popular

import com.gonpas.wembleymoviesapp.R
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.gonpas.wembleymoviesapp.MainActivity
import com.gonpas.wembleymoviesapp.network.StubRemoteService.movieDto1
import com.gonpas.wembleymoviesapp.repository.CustomRepositoryImpl
import com.gonpas.wembleymoviesapp.repository.MoviesRepository
import com.gonpas.wembleymoviesapp.utils.ItemChildViewVisibilityAssertion.Companion.isItemChildViewNotVisible
import com.gonpas.wembleymoviesapp.utils.ItemChildViewVisibilityAssertion.Companion.isItemChildViewVisible
import com.gonpas.wembleymoviesapp.utils.ItemChildTextViewCorrectText.Companion.isTextItemChildCorrect
import com.gonpas.wembleymoviesapp.utils.MoviesAdapter
import com.gonpas.wembleymoviesapp.utils.ToastMatcher
import com.gonpas.wembleymoviesapp.utils.clickItemChildWithId
import com.gonpas.wembleymoviesapp.utils.launchFragmentInHiltContainer
import com.gonpas.wembleymoviesapp.utils.typeTextAtSearchView
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Test
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

@MediumTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
//@RunWith(AndroidJUnit4::class) -> se ejecuta con el CustomTestRunner definido en el build.gradle, el cual hereda hereda de AndroidJUnitRunner
class PopularMoviesFragmentAndroidTest {

    @Inject lateinit var repository: MoviesRepository
//    private val dataBindingIdlingResource = DataBindingIdlingResource()


    val testMovie = movieDto1

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)


    @Before
    fun setup(){
        hiltRule.inject()

//        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
//        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        (repository as CustomRepositoryImpl).closeDb()

//        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
//        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun onCreateViewTest()  {
        launchFragmentInHiltContainer<PopularMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)
        onView(withId(R.id.list_popular_movies)).check(matches(isDisplayed()))
//        Thread.sleep(2000)
    }

    @Test
    fun checkNetworkError()  {
        (repository as CustomRepositoryImpl).setReturnError(true)

         launchFragmentInHiltContainer<PopularMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)

        onView(withText("java.net.UnknownHostException: Unable to resolve host \"api.themoviedb.org\": No address associated with hostname"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun viewTextHasCorrectText(){
        launchFragmentInHiltContainer<PopularMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)

//        Thread.sleep(1000)
        val voteAverageTemplateFormated = getApplicationContext<HiltTestApplication>()
            .resources.getString(R.string.calificacion)
            .format(testMovie.voteAverage.toString().replace('.',','))

        val date = testMovie.releaseDate.split("-")
        val formatedDate = getApplicationContext<HiltTestApplication>()
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

//        Thread.sleep(1000)
    }

    @Test
    fun isSaveFabVisible() {
        launchFragmentInHiltContainer<PopularMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)

//        Thread.sleep(1000)
        onView(withId(R.id.list_popular_movies))
            .check(
                isItemChildViewVisible(R.id.floatingActionButton)
            )
//        Thread.sleep(1000)
    }

    @Test
    fun saveMovie_HideSaveFab() {
         launchFragmentInHiltContainer<PopularMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)

//        Thread.sleep(1000)
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
//        Thread.sleep(2000)
    }

    @Test
    fun clickOverview_showDialog(){
         launchFragmentInHiltContainer<PopularMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)

        onView(withId(R.id.list_popular_movies))
            .perform(
                RecyclerViewActions
                    .actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(0,
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
    fun searchMovies_showResult() {
         launchFragmentInHiltContainer<PopularMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)

        onView(withId(R.id.searchView)).perform(typeTextAtSearchView("Vientos"))

        Thread.sleep(500)
        onView(withId(R.id.list_popular_movies)).check(matches(hasChildCount(1)))
        onView(withId(R.id.list_popular_movies)).check(matches(hasDescendant(withText("Vientos del norte"))))
        onView(withId(R.id.pop_list_item)).check(matches(not(hasSibling(withId(R.id.pop_list_item)))))

//        Thread.sleep(2000)
        onView(withId(R.id.noFound)).check(matches(not(isDisplayed())))
    }

    @Test
    fun searchMovies_noFound_showNoFoundAdvice(){
         launchFragmentInHiltContainer<PopularMoviesFragment>(null, R.style.Theme_WembleyMoviesApp)

//        Thread.sleep(2000)

        onView(withId(R.id.searchView)).perform(typeTextAtSearchView("probando"))

        onView(withId(R.id.noFound)).check(matches(isDisplayed()))
    }
}