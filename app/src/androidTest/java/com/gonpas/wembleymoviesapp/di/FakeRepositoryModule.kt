package com.gonpas.wembleymoviesapp.di

import com.gonpas.wembleymoviesapp.repository.CustomRepositoryImpl
import com.gonpas.wembleymoviesapp.repository.FakeRepositoryImpl
import com.gonpas.wembleymoviesapp.repository.MoviesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Qualifier

//@Qualifier
//annotation class FakeRepository

//@Qualifier
//annotation class CustomTestRepository

//@TestInstallIn(
//    components = [SingletonComponent::class],
//    replaces = [RepositoryModule::class]
//)
//@Module
//abstract class FakeRepositoryModule {
////    @FakeRepository
//    @Binds
//    abstract fun bindFakeRepository(impl: FakeRepositoryImpl): MoviesRepository
//}

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
@Module
abstract class CustomRepository {
//    @CustomTestRepository
    @Binds
    abstract fun bindCustomRepository(impl: CustomRepositoryImpl): MoviesRepository
}
