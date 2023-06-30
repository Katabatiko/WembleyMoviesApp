package com.gonpas.wembleymoviesapp

import android.util.Log
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.gonpas.wembleymoviesapp.network.StubTestRemoteService.movieDto1
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class RoboMainActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityScenario = ActivityScenarioRule(MainActivity::class.java)

//    @Before
//    fun setup() {
//        hiltRule.inject()
//    }

    @Test
    fun generateInitialPagerFragmentsTest() {

        assertThat(tab.tabCount, `is`(2))
        assertThat(tab.getTabAt(0)?.text ?:"error", `is`("Populares"))
        assertThat(tab.getTabAt(1)?.text ?:"error", `is`("Favoritas"))

        assertThat(pager.adapter?.itemCount ?: "error", `is`(2))
        assertThat(pager.childCount, `is`(1))
    }

    @Test
    fun createMoviePage() {
        /*val film = DomainFilm(
            movieDto4.id,
            movieDto4.title,
            movieDto4.overview,
            creditsDto.cast.asList().asListDomainModel(
                "https://image.tmdb.org/t/p/%s"
            ),
            creditsDto.crew.asList().asListDomainModel()
        )*/
        // se comprueba que solo hay dos pestañas
        assertThat(tab.tabCount, `is`(2))
        assertThat(pager.adapter?.itemCount ?: "error", `is`(2))

        // se crean los nuevos elementos
        MainActivity.addOrReplaceMovieTab(movieDto1.title)

        assertThat(tab.tabCount, `is`(3))
        assertThat(pager.adapter?.itemCount ?: "error", `is`(3))

    }
}