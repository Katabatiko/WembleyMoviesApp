package com.gonpas.wembleymoviesapp.network

import com.gonpas.wembleymoviesapp.domain.DomainMovie
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImagesDto (
    @Json(name = "base_url") val baseUrl: String,
    @Json(name = "secure_base_url") val secureBaseUrl: String,
    @Json(name = "backdrop_sizes") val backdropSizes: Array<String>,
    @Json(name = "logo_sizes") val logoSizes: Array<String>,
    @Json(name = "poster_sizes") val posterSizes: Array<String>,
    @Json(name = "profile_sizes") val profileSizes: Array<String>,
    @Json(name = "still_sizes") val stillSizes: Array<String>
)

@JsonClass(generateAdapter = true)
data class Configuration (
    val images: ImagesDto,
    @Json(name = "change_keys") val changeKeys: Array<String>
)

@JsonClass(generateAdapter = true)
data class MovieDto (
    val adult: Boolean,
    @Json(name = "backdrop_path") val backdropPath: String?,
    val genre_ids: Array<Int>,
    val id: Int,
    @Json(name = "original_language") val originalLanguage: String,
    @Json(name = "original_title") val originalTitle: String,
    val overview: String,
    val popularity: Float,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "release_date") val releaseDate: String = "",
    val title: String,
    val video: Boolean,
    @Json(name = "vote_average") val voteAverage: Float,
    @Json(name = "vote_count") val voteCount: Int
)

fun List<MovieDto>.asListDomainMovies(baseUrl: String, imgSize: String): List<DomainMovie>{
    val template = "%s%s/%s"
    return map {
        DomainMovie(
            backdropPath = it.backdropPath,
            id = it.id,
            title = it.title,
            overview = it.overview,
            imgUrl = template.format(baseUrl, imgSize, it.posterPath),
            popularity = it.popularity,
            releaseDate = it.releaseDate,
            voteAverage = it.voteAverage,
            voteCount = it.voteCount
        )
    }
}

@JsonClass(generateAdapter = true)
data class MoviesListDto (
    val page: Int,
    val results: Array<MovieDto>,
    @Json(name= "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
)

@JsonClass(generateAdapter = true)
data class RequestToken(
    @Json(name = "request_token")
    val requestToken: String
)
@JsonClass(generateAdapter = true)
data class RequestedToken(
    val success: Boolean,
    @Json(name = "expires_at")
    val expiresAt: String,
    @Json(name = "request_token")
    val requestToken: String
)