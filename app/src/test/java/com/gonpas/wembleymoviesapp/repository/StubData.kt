package com.gonpas.wembleymoviesapp.repository

import androidx.lifecycle.MutableLiveData
import com.gonpas.wembleymoviesapp.database.MovieDb
import com.gonpas.wembleymoviesapp.domain.DomainMovie
import com.gonpas.wembleymoviesapp.network.Configuration
import com.gonpas.wembleymoviesapp.network.ImagesDto
import com.gonpas.wembleymoviesapp.network.MovieDto
import com.gonpas.wembleymoviesapp.network.MoviesListDto

/*********************************** DTOs ***********************************/
    val images = ImagesDto(
        baseUrl = "http://image.tmdb.org/t/p/",
        secureBaseUrl = "https://image.tmdb.org/t/p/",
        backdropSizes = arrayOf("w300","w780","w1280","original"),
        logoSizes = arrayOf("w45","w92","154","w185","w300","w500","original"),
        posterSizes = arrayOf("w92","w154","w185","w342","w500","w780","original"),
        profileSizes = arrayOf("w45","w185","632","original"),
        stillSizes = arrayOf("w92","w185","w300","original")
    )
    val conf = Configuration(
        images,
        changeKeys = arrayOf("")
        // "adult","air_date","also_known_as","alternative_titles","biography","birthday","budget","cast","certifications","character_names",
        //        "created_by","crew","deathday","episode","episode_number","episode_run_time","freebase_id","freebase_mid","general",
        //        "genres","guest_stars","homepage","images","imdb_id","languages","name","network","origin_country","original_name",
        //        "original_title","overview","parts","place_of_birth","plot_keywords","production_code","production_companies",
        //        "production_countries","releases","revenue","runtime","season","season_number","season_regular","spoken_languages",
        //        "status","tagline","title","translations","tvdb_id","tvrage_id","type","video","videos"
    )

    val movieDto1 = MovieDto(
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
    val movieDto2 = MovieDto(
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
    val movieDto3 = MovieDto(
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

    val result = arrayOf(movieDto1, movieDto3, movieDto2)

    var moviesDto: MoviesListDto = MoviesListDto(
        page = 1,
        results = result,
        totalPages = 1,
        totalResults = 3
    )


/******************** DOMAIN *********************************/
    val domainMovie1 = DomainMovie(
        id = 100,
        title = "El test del algoritmo",
        overview = "Probando las funcionalidades del PopularMoviesViewModel para su lanzamiento a producción.",
        releaseDate = "2023-04-08",
        backdropPath = null,
        imgUrl = null,
        popularity = 7.7f,
        voteAverage = 8.8f,
        voteCount = 1000,
        fav = true
    )
    val domainMovie2 = DomainMovie(
        id = 101,
        title = "El test del algoritmo 2",
        overview = "Se sigue probando las funcionalidades del PopularMoviesViewModel para su lanzamiento a producción.",
        releaseDate = "2023-04-08",
        backdropPath = null,
        imgUrl = null,
        popularity = 6.7f,
        voteAverage = 7.8f,
        voteCount = 1100,
        fav = false
    )

/*********************************** DATABASE *************************/
val movieDb1 = MovieDb(
    movieId = 100,
    title = "El test del algoritmo",
    overview = "Probando las funcionalidades del PopularMoviesViewModel para su lanzamiento a producción.",
    releaseDate = "2023-04-08",
    backdropPath = null,
    imgUrl = null,
    popularity = 7.7f,
    voteAverage = 8.8f,
    voteCount = 1000
)
val movieDb2 = MovieDb(
    backdropPath = null,
    movieId = 10002,
    title = "Vientos del norte",
    overview = "Es la historia de Boreal",
    imgUrl = null,
    releaseDate = "2023-3-3",
    popularity = 6.5f,
    voteAverage = 8.8f,
    voteCount = 535
)