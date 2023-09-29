package com.gonpas.wembleymoviesapp.network

import com.gonpas.wembleymoviesapp.BuildConfig
import retrofit2.http.*

private const val API_KEY = BuildConfig.apiKey


interface TmdbApiService {
    /*@GET("authentication/token/new")
    suspend fun getToken(@Query("api_key") api_key: String = API_KEY): RequestedToken

    @POST("authentication/session/new")
    suspend fun getSessionId(
        @Body requestToken: RequestToken,
        @Query("api_key") api_key: String = API_KEY
    )*/

    @GET("configuration")
    suspend fun getConfiguration(@Query("api_key") api_key: String = API_KEY): Configuration

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int =1
    ): MoviesListDto

    @GET("search/movie")
    suspend fun searchMovie(
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int =1,
        @Query("query") query: String
    ) : MoviesListDto

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "es-ES"
    ): CreditsDto

    @GET("person/{person_id}")
    suspend fun getPerson(
        @Path("person_id") person_id: Int,
        @Query("api_key") api_key: String = API_KEY,
        @Query("language") language: String = "es-ES"
    ): PersonDto
}