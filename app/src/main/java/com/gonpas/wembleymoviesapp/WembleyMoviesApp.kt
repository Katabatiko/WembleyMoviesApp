package com.gonpas.wembleymoviesapp

import android.app.Application
import com.gonpas.wembleymoviesapp.repository.InterfaceMoviesRepository

/**
 * An application that lazily provides a repository. Note that this Service Locator pattern is
 * used to simplify the sample. Consider a Dependency Injection framework.
 */
class WembleyMoviesApp : Application() {

    val moviesRepository: InterfaceMoviesRepository
        get() = ServiceLocator.provideMoviesRepository(this)


}