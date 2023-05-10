package com.gonpas.wembleymoviesapp.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val API_KEY = "Insert here The Movie Db API_KEY"
private const val TMBD_BASE_URL = "https://api.themoviedb.org/3/"


private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

interface TmdbApiService {
    @GET("authentication/token/new")
    suspend fun getToken(@Query("api_key") api_key: String = API_KEY): RequestedToken

    @POST("authentication/session/new")
    suspend fun getSessionId(
        @Body requestToken: RequestToken,
        @Query("api_key") api_key: String = API_KEY
    )

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


object TmdbApi {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(TMBD_BASE_URL)
        .build()

    val tmdbApiService: TmdbApiService = retrofit.create(TmdbApiService::class.java)
}