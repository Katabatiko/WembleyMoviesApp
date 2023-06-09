package com.gonpas.wembleymoviesapp.network

import android.os.Parcelable
import com.gonpas.wembleymoviesapp.domain.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


/**
 * AUTENTICACION
 */
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

/**
 * CONFIGURACION
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class ImagesDto (
    @Json(name = "base_url")
    val baseUrl: String,
    @Json(name = "secure_base_url")
    val secureBaseUrl: String,
    @Json(name = "backdrop_sizes")
    val backdropSizes: Array<String>,
    @Json(name = "logo_sizes")
    val logoSizes: Array<String>,
    @Json(name = "poster_sizes")
    val posterSizes: Array<String>,
    @Json(name = "profile_sizes")
    val profileSizes: Array<String>,
    @Json(name = "still_sizes")
    val stillSizes: Array<String>
): Parcelable

fun ImagesDto.asDomainModel(): DomainImages{
    return DomainImages(
        baseUrl = this.baseUrl,
        secureBaseUrl = this.secureBaseUrl,
        backdropSizes = this.backdropSizes,
        logoSizes = this.logoSizes,
        posterSizes = this.posterSizes,
        profileSizes = this.profileSizes,
        stillSizes = this.stillSizes
    )
}

@Parcelize
@JsonClass(generateAdapter = true)
data class Configuration (
    val images: ImagesDto,
    @Json(name = "change_keys") val changeKeys: Array<String>
): Parcelable

fun Configuration.asDomainModel(): DomainConfiguration{
    return DomainConfiguration(
        images = this.images,
        changeKeys = this.changeKeys
    )
}

/**
 * PELICULAS
 */
@JsonClass(generateAdapter = true)
data class MovieDto (
    val adult: Boolean,
    @Json(name = "backdrop_path")
    val backdropPath: String?,
    val genre_ids: Array<Int>,
    val id: Int,
    @Json(name = "original_language")
    val originalLanguage: String,
    @Json(name = "original_title")
    val originalTitle: String,
    val overview: String,
    val popularity: Float,
    @Json(name = "poster_path")
    val posterPath: String?,
    @Json(name = "release_date")
    val releaseDate: String = "",
    val title: String,
    val video: Boolean,
    @Json(name = "vote_average")
    val voteAverage: Float,
    @Json(name = "vote_count")
    val voteCount: Int
)

fun List<MovieDto>.asListDomainMovies(casiUrl: String): List<DomainMovie>{
    return map {
        DomainMovie(
            backdropPath = it.backdropPath,
            id = it.id,
            title = it.title,
            overview = it.overview,
            imgUrl = casiUrl.format(it.posterPath),
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
    @Json(name= "total_pages")
    val totalPages: Int,
    @Json(name = "total_results")
    val totalResults: Int
)

/**
 * CREDITOS
 */
@JsonClass(generateAdapter = true)
data class ActoresDto (
    val adult: Boolean,
    val gender: Int?,
    val id: Int,
    @Json(name= "known_for_department")
    val knownForDepartment: String,
    val name: String,
    @Json(name = "original_name")
    val originalName: String,
    val popularity: Float,
    @Json(name = "profile_path")
    val profilePath: String?,
    @Json(name = "cast_id")
    val castId: Int,
    val character: String,
    @Json(name = "credit_id")
    val creditId: String,
    val order: Int
)

fun List<ActoresDto>.asListDomainModel(casiUrl: String): List<DomainActor>{
    return map {
        DomainActor(
            personId = it.id,
            name = it.name,
            character = it.character,
            profilePath = casiUrl.format(it.profilePath)
        )
    }
}

@JsonClass(generateAdapter = true)
data class CrewDto (
    val adult: Boolean,
    val gender: Int?,
    val id: Int,
    @Json(name= "known_for_department")
    val knownForDepartment: String,
    val name: String,
    @Json(name = "original_name")
    val originalName: String,
    val popularity: Float,
    @Json(name = "profile_path")
    val profilePath: String?,
    @Json(name = "credit_id")
    val creditId: String,
    val department: String,
    val job: String
)
fun List<CrewDto>.asListDomainModel(): List<DomainCrew>{
    return map {
        DomainCrew(
            id = it.id,
            name = it.name,
            job = it.job
        )
    }
}

@JsonClass(generateAdapter = true)
data class CreditsDto(
    val id: Int,
    val cast: Array<ActoresDto>,
    val crew: Array<CrewDto>
)

@JsonClass(generateAdapter = true)
data class PersonDto(
    val birthday: String?,
    @Json(name = "known_for_department")
    val knowForDepartment: String,
    val deathday: String?,
    val id: Int,
    val name: String,
    @Json(name = "also_known_as")
    val alsoKnownAs: Array<String>,
    val gender: Int,
    val biography: String,
    val popularity: Float,
    @Json(name = "place_of_birth")
    val placeOfBirth: String?,
    @Json(name = "profile_path")
    val profilePath: String?,
    val adult: Boolean,
    @Json(name = "imdb_id")
    val imdbId: String?,
    val homepage: String?
)
fun PersonDto.asDomainModel(casiUrl: String): DomainPerson{
    return DomainPerson(
                birthday    = this.birthday,
                deathday    = this.deathday,
                id          = this.id,
                name        = this.name,
                alsoKnownAs = this.alsoKnownAs,
                biography   = this.biography,
                placeOfBirth = this.placeOfBirth,
                profilePath = casiUrl.format(this.profilePath),
                imdbId      = this.imdbId,
                homepage    = this.homepage
            )
}