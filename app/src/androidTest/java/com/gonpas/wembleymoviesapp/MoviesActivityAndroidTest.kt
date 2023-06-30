package com.gonpas.wembleymoviesapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.gonpas.wembleymoviesapp.network.StubRemoteService.creditsDto
import com.gonpas.wembleymoviesapp.network.StubRemoteService.evangeline
import com.gonpas.wembleymoviesapp.network.StubRemoteService.movieDto1
import com.gonpas.wembleymoviesapp.network.StubRemoteService.movieDto4
import com.gonpas.wembleymoviesapp.utils.MoviesAdapter
import com.gonpas.wembleymoviesapp.utils.ToastMatcher
import com.gonpas.wembleymoviesapp.utils.ViewPager2Actions
import com.gonpas.wembleymoviesapp.utils.clickItemChildWithId
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val TAG = "xxMaat"


@LargeTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
//@RunWith(AndroidJUnit4::class) -> se ejecuta con el CustomTestRunner definido en el build.gradle, el cual hereda hereda de AndroidJUnitRunner
class MoviesActivityAndroidTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityScenario = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun saveMovie_showInfoToast_showMovieInFavsTab()  {

        // comprobar que no hay favoritos
        activityScenario.scenario.onActivity {
            pager.currentItem = 1
        }
        onView(withText(getInstrumentation().targetContext.getString(R.string.no_hay_favoritas))).check( matches( isDisplayed() ) )
//        Thread.sleep(1000)

        activityScenario.scenario.onActivity {
            pager.currentItem = 0
        }
        // guardar una movie en favoritos
        onView(withId(R.id.list_popular_movies)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                2,
                clickItemChildWithId(R.id.floatingActionButton)
            )
        )

        // control del toast informativo
        onView(withText("${ getInstrumentation().targetContext.getString(R.string.guardada) }: '${movieDto4.title}'"))
            .inRoot( ToastMatcher() )
            .check(matches(isDisplayed()))

//        Thread.sleep(2000)
        activityScenario.scenario.onActivity {
            pager.currentItem = 1
        }
//        Thread.sleep(1000)

        // comprobar que la pelicula se muestra en favoritos
        onView(withId(R.id.list_fav_movies))
            .check(matches(hasDescendant(withText(movieDto4.title))))
//            .check(isTextItemChildCorrect(R.id.titulo, movieDto4.title))
//        Thread.sleep(1000)

        activityScenario.scenario.close()
    }

    @Test
    fun deleteFavMovie() {

        // guardar una movie en favoritos
        onView(withId(R.id.list_popular_movies)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                2,
                clickItemChildWithId(R.id.floatingActionButton)
            )
        )

//        Thread.sleep(1000)
        // ir a fav tab
        activityScenario.scenario.onActivity {
            pager.currentItem = 1
        }

        // pulsar en el fab para borrar la pelicula
        onView(withId(R.id.list_fav_movies)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                0,
                clickItemChildWithId(R.id.floatingActionButton)
            )
        )
//        Thread.sleep(1000)
        // aceptar el dialogo de confirmación
        onView(withText(R.string.confirmar)).perform( click() )

         // comprobar que la película ya no está y se muestra el mensaje sin favoritas
        onView(withId(R.id.list_fav_movies))
            .check(matches(not(hasDescendant(withText(movieDto4.title)))))
        onView(withText(R.string.no_hay_favoritas)).check( matches( isDisplayed() ) )
//        Thread.sleep(1000)

        activityScenario.scenario.close()
    }

    @Test
    fun clickMovie_showItInTabMovie() {

//        Thread.sleep(1000)
        // clickar en una pelicula
        onView(withId(R.id.list_popular_movies))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                    2,
                    clickItemChildWithId(R.id.mas)
                )
            )

//        Thread.sleep(1000)
        // comprobar que se muestra la película clickada
        onView(withId(R.id.movie_title)).check(matches(withText(movieDto4.title)))
        val director = creditsDto.crew.filter{ it.job == "Director" }[0].name
        onView(withId(R.id.movie_director)).check(
            matches(withText(
                "Director: $director"
            ))
        )
        onView(withId(R.id.full_overview)).check(matches(withText(movieDto4.overview)))

//        Thread.sleep(1000)

        activityScenario.scenario.close()
    }

    @Test
    fun saveMovie_clickMovieInFavTab_showItInTabMovie_backToPopTab_clickOtherMovie_showNewMovieInTabMovie() {

        Thread.sleep(1)
        // guardar una pelicula en favoritos
        onView(withId(R.id.list_popular_movies))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                    2,
                    clickItemChildWithId(R.id.floatingActionButton)
                )
            )

        // comprobar que se muestra en fav tab y clickar para mostrar en movie tab
        activityScenario.scenario.onActivity {
            pager.currentItem = 1
        }
        onView(withId(R.id.viewPager)).perform(
            ViewPager2Actions.scrollRight()
        )


//        Thread.sleep(1000)

        onView(withId(R.id.list_fav_movies))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                    0,
                    clickItemChildWithId(R.id.more)
                )
            )
        // redundante pero necesario para esperar a que el pager muestre la pestaña
        onView(withId(R.id.viewPager)).perform(
            ViewPager2Actions.scrollToLast()
        )

        // comprobar que se muestra la película
        onView(withId(R.id.movie_title)).check(matches(withText(movieDto4.title)))
//        Thread.sleep(1000)

        // volver a popular tab y clickar otra película
            activityScenario.scenario.onActivity {
                pager.currentItem = 0
            }
        onView(withId(R.id.viewPager)).perform(
                    ViewPager2Actions.scrollToFirst()
            )
//        Thread.sleep(1000)

        onView(withId(R.id.list_popular_movies))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                    0,
                    clickItemChildWithId(R.id.mas)
                )
            )

//        Thread.sleep(1000)
        // comprobar que se muestra en movie tab la nueva película clickada
        onView(withId(R.id.movie_title)).check(matches(withText(movieDto1.title)))

        activityScenario.scenario.close()
    }

    @Test
    fun clickOnCastItem_showDialogPerson() {
//        Thread.sleep(1000)
        // clickar en una pelicula
        onView(withId(R.id.list_popular_movies))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                    2,
                    clickItemChildWithId(R.id.mas)
                )
            )

        Thread.sleep(500)
        // clickar en actor/triz
        onView(withId(R.id.casting)).perform(
            RecyclerViewActions.actionOnItemAtPosition<MoviesAdapter.MovieViewHolder>(
                1,
                clickItemChildWithId(R.id.biografia)
            )
        )
//        Thread.sleep(1000)
        // comprobar que se muestra el dialogo de persona y la informacion correcta
        onView(withText(android.R.string.ok))
            .inRoot(RootMatchers.isDialog())
            .check(
                matches(isDisplayed())
            )

        onView(withId(R.id.person_name))
            .inRoot(RootMatchers.isDialog())
            .check(
                matches(withText(creditsDto.cast[1].name))
            )

        onView(withId(R.id.person_birthplace))
            .inRoot(RootMatchers.isDialog())
            .check(
                matches(withText(evangeline.placeOfBirth))
            )
        // adaptar al formato en que se muestra
        val partes = evangeline.birthday!!.split("-")
        val cumpleannos = "Fecha de nacimiento: %s/%s/%s".format(partes[2], partes[1], partes[0])

        onView(withId(R.id.person_birthday))
            .inRoot(RootMatchers.isDialog())
            .check(
                matches(withText(cumpleannos))
            )
//        Thread.sleep(1000)
        activityScenario.scenario.close()
    }
}
