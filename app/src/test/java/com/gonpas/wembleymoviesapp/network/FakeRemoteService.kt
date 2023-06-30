package com.gonpas.wembleymoviesapp.network

import kotlinx.coroutines.delay
import java.net.UnknownHostException

object FakeRemoteService: TmdbApiService {
    private const val SERVICE_LATENCY = 0L


    // para la simulacion de errores
    private var shouldReturnError = false
    fun setReturnError(value: Boolean){
        shouldReturnError = value
    }


    /*override suspend fun getToken(api_key: String): RequestedToken {}*/
    /*override suspend fun getSessionId(requestToken: RequestToken, api_key: String) {}*/

    override suspend fun getConfiguration(api_key: String): Configuration {
        delay(SERVICE_LATENCY)
        return StubTestRemoteService.configuration
    }

    override suspend fun getPopularMovies(
        api_key: String,
        language: String,
        page: Int
    ): MoviesListDto {
        delay(SERVICE_LATENCY)

        return if (!shouldReturnError)      StubTestRemoteService.actualMoviesDto
                else throw UnknownHostException("Unable to resolve host \"api.themoviedb.org\": No address associated with hostname")
    }

    override suspend fun searchMovie(
        api_key: String,
        language: String,
        page: Int,
        query: String
    ): MoviesListDto {
        delay(SERVICE_LATENCY)
        val newResult =  StubTestRemoteService.actualMoviesDto.results.filter {
            it.title.contains(query)
        }
        val results = newResult.toTypedArray()
        return MoviesListDto(
            page= page,
            results = results,
            totalPages = 1,
            totalResults = newResult.size
        )
    }

    override suspend fun getMovieCredits(
        movie_id: Int,
        api_key: String,
        language: String
    ): CreditsDto {
        delay(SERVICE_LATENCY)
        return  when(movie_id){
            640146  -> StubTestRemoteService.creditsDto
            100000  -> StubTestRemoteService.creditsDtoSpecial
            100002  -> StubTestRemoteService.creditsDtoSpecial2
            else    -> StubTestRemoteService.creditsDtoEmpty
        }
    }

    override suspend fun getPerson(person_id: Int, api_key: String, language: String): PersonDto {
        delay(SERVICE_LATENCY)
        return  if (person_id == 19034) StubTestRemoteService.person1
                else                    StubTestRemoteService.nobody
    }
}