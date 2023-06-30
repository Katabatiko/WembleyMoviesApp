package com.gonpas.wembleymoviesapp.di

import javax.inject.Qualifier


annotation class Despachadores {
    @Qualifier
    annotation class Main

    @Qualifier
    annotation class IO

    @Qualifier
    annotation class Test
}