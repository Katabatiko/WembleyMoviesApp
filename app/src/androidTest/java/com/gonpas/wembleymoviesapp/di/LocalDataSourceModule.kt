package com.gonpas.wembleymoviesapp.di

//import com.gonpas.wembleymoviesapp.database.FakeAndroidTestLocalDataSource
import com.gonpas.wembleymoviesapp.database.LocalTestingDbSourceImpl
import com.gonpas.wembleymoviesapp.database.MoviesDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Qualifier
import javax.inject.Singleton

//@Qualifier
//annotation class FakeLocalDataSource
//
//@Qualifier
//annotation class TestingDbDataSource

/*@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
@Module
abstract class FakeLocalDataSourceModule {
    @FakeLocalDataSource
    @Singleton
    @Binds
    abstract fun bindFakeMoviesDao(impl: FakeAndroidTestLocalDataSource): MoviesDao
}*/

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
@Module
abstract class TestingDbDataSourceModule {
//    @TestingDbDataSource
    @Singleton
    @Binds
    abstract fun bindTestingDbMoviesDao(impl: LocalTestingDbSourceImpl): MoviesDao
}