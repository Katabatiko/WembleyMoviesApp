package com.gonpas.wembleymoviesapp.network

import kotlinx.coroutines.delay

object FakeRemoteService: TmdbApiService {

    private const val SERVICE_LATENCY = 1000L

    private val imagesDto: ImagesDto
    private val configuration: Configuration
    private val results: Array<MovieDto>
    var actualMoviesDto: MoviesListDto



    private val movieDto1 = MovieDto(
        adult = false,
        backdropPath = null,
        id = 10000,
        title = "El test de Navarrevisca",
        overview = "Es la historia de un test rural",
        posterPath = "postername.jpg",
        releaseDate = "2023-1-1",
        popularity = 9.9f,
        genre_ids = arrayOf(1,2),
        originalLanguage = "es-ES",
        originalTitle = "The test from Navarrevisca",
        video = false,
        voteAverage = 9.9f,
        voteCount = 501
    )
    private val movieDto2 = MovieDto(
        adult = false,
        backdropPath = null,
        id = 10001,
        title = "El test de Navalosa",
        overview = "Es la historia de un test del campo rural",
        posterPath = null,
        releaseDate = "2023-2-2",
        popularity = 9.9f,
        genre_ids = arrayOf(4,9),
        originalLanguage = "es-ES",
        originalTitle = "The test from Navalosa",
        video = false,
        voteAverage = 9.9f,
        voteCount = 500
    )
    private val movieDto3 = MovieDto(
        adult = false,
        backdropPath = null,
        id = 10002,
        title = "Vientos del norte",
        overview = "Es la historia de Boreal",
        posterPath = null,
        releaseDate = "2023-3-3",
        popularity = 6.5f,
        genre_ids = arrayOf(6,4),
        originalLanguage = "es-ES",
        originalTitle = "North wind",
        video = false,
        voteAverage = 8.8f,
        voteCount = 535
    )



    init {
        imagesDto = ImagesDto(
            baseUrl = "http://image.tmdb.org/t/p/",
            secureBaseUrl = "https://image.tmdb.org/t/p/",
            backdropSizes = arrayOf("w300","w780","w1280","original"),
            logoSizes = arrayOf("w45","w92","154","w185","w300","w500","original"),
            posterSizes = arrayOf("w92","w154","w185","w342","w500","w780","original"),
            profileSizes = arrayOf("w45","w185","632","original"),
            stillSizes = arrayOf("w92","w185","w300","original")
        )
        configuration = Configuration(
            imagesDto,
            changeKeys = arrayOf("")
            // "adult","air_date","also_known_as","alternative_titles","biography","birthday","budget","cast","certifications","character_names",
            //        "created_by","crew","deathday","episode","episode_number","episode_run_time","freebase_id","freebase_mid","general",
            //        "genres","guest_stars","homepage","images","imdb_id","languages","name","network","origin_country","original_name",
            //        "original_title","overview","parts","place_of_birth","plot_keywords","production_code","production_companies",
            //        "production_countries","releases","revenue","runtime","season","season_number","season_regular","spoken_languages",
            //        "status","tagline","title","translations","tvdb_id","tvrage_id","type","video","videos"
        )

        results = arrayOf(movieDto1, movieDto3, movieDto2)
        actualMoviesDto = MoviesListDto(
            1,
            results,
            1,
            3
        )
    }

    override suspend fun getToken(api_key: String): RequestedToken {
        TODO("Not yet implemented")
    }

    override suspend fun getSessionId(requestToken: RequestToken, api_key: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getConfiguration(api_key: String): Configuration {
        delay(SERVICE_LATENCY)
        return configuration
    }

    override suspend fun getPopularMovies(
        api_key: String,
        language: String,
        page: Int
    ): MoviesListDto {
        delay(SERVICE_LATENCY)
        return actualMoviesDto
    }

    override suspend fun searchMovie(
        api_key: String,
        language: String,
        page: Int,
        query: String
    ): MoviesListDto {
        delay(SERVICE_LATENCY)
        val newResult =  actualMoviesDto.results.filter {
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
}