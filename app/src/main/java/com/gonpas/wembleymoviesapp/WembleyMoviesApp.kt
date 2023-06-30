package com.gonpas.wembleymoviesapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * An application that lazily provides a repository. Note that this Service Locator pattern is
 * used to simplify the sample. Consider a Dependency Injection framework.
 */
@HiltAndroidApp
class WembleyMoviesApp : Application() {}