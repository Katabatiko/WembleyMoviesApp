package com.gonpas.wembleymoviesapp.utils

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {
    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

// Espresso does not work well with coroutines yet. See
// https://github.com/Kotlin/kotlinx.coroutines/issues/982
inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    EspressoIdlingResource.increment()  // ocupado
    return try {
        function()
    }finally {
        EspressoIdlingResource.decrement()  // en reposo
    }
}
