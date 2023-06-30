package com.gonpas.wembleymoviesapp.domain

import android.os.Parcelable
import com.gonpas.wembleymoviesapp.database.MovieDb
import com.gonpas.wembleymoviesapp.network.ImagesDto
import com.gonpas.wembleymoviesapp.utils.smartTruncate
import kotlinx.parcelize.Parcelize
/**
 * Los domain objects son los objetos que maneja kotlin y que serán los que
 * manejen la app y se muestren en la pantalla
 */
@Parcelize
data class DomainImages(
    val baseUrl: String,
    val secureBaseUrl: String,
    val backdropSizes: Array<String>,
    val logoSizes: Array<String>,
    val posterSizes: Array<String>,
    val profileSizes: Array<String>,
    val stillSizes: Array<String>
): Parcelable
@Parcelize
data class DomainConfiguration(
    val images: ImagesDto,
    val changeKeys: Array<String>
): Parcelable


@Parcelize
data class DomainMovie(
    val backdropPath: String?,
    val id: Int,
    val title: String,
    val overview: String,
    val imgUrl: String?,
    val popularity: Float,
    val releaseDate: String,
    val voteAverage: Float,
//    val genresIds: Array<Int>,
    val voteCount: Int,
    var fav: Boolean = false
) : Parcelable{
    val shortOverview: String
        get() = overview.smartTruncate(140)
}

fun DomainMovie.asMovieDb() : MovieDb{
    return MovieDb(
        backdropPath = this.backdropPath,
        movieId = this.id,
        title = this.title,
        overview = this.overview,
        imgUrl = this.imgUrl,
        popularity = this.popularity,
        releaseDate = this.releaseDate,
        voteAverage = this.voteAverage,
        voteCount = this.voteCount,
    )
}

@Parcelize
data class DomainFilm(
    val filmId: Int,
    val title: String,
    val overview: String,
    val cast: List<DomainActor>,
    val crew: List<DomainCrew>
): Parcelable {

    fun getDirector(): String {
        val directores = crew.filter { it.job == "Director" }
        val size = directores.size

        return if (size != 0) {
                    if (size == 1)  directores[0].name
                    else{
                        var direccion = "%s"
                        directores.forEachIndexed { index, domainCrew ->
                            direccion = if (index == size -1) direccion.format(domainCrew.name)
                                        else "${ direccion.format(domainCrew.name) }, %s"
                        }
                        return direccion
                    }
                } else  "?"
    }

    fun getGuionista(): String {
        var guionistas = crew.filter { it.job == "Screenplay"}
        var size = guionistas.size

        if (size == 0)      guionistas = crew.filter { it.job == "Writer" }
        size = guionistas.size

        return if (size != 0) {
                    if (size == 1)  guionistas[0].name
                    else{
                        var guion = "%s"
                        guionistas.forEachIndexed { index, domainCrew ->
                            guion = if (index == size -1) guion.format(domainCrew.name)
                                    else "${ guion.format(domainCrew.name) }, %s"
                        }
                        return guion
                    }
                } else  "?"
    }

    fun getCompositor(): String {
        val compositor = crew.filter { it.job == "Original Music Composer" }

        return  if (compositor.isNotEmpty())
                    compositor[0].name
                else
                    "?"
    }

    fun getFotoDirector(): String {
        val fotografia = crew.filter { it.job == "Director of Photography" }

        return  if (fotografia.isNotEmpty())
                    fotografia[0].name
                else
                    "?"
    }

    fun getStoryBy(): String {
        var writer = crew.filter { it.job == "Novel" }

        return  if (writer.isNotEmpty())
                    writer[0].name
                else {
                    writer = crew.filter { it.job == "Story" }

                    if (writer.isNotEmpty())                writer[0].name
                    else                                    "?"
                }
    }
}
@Parcelize
data class DomainActor(
    val personId: Int,
    val name: String,
    val character: String,
    val profilePath: String?
): Parcelable

@Parcelize
data class DomainCrew(
    val id: Int,
    val name: String,
    val job: String
): Parcelable

@Parcelize
data class DomainPerson(
    val birthday: String?,
    val deathday: String?,
    val id: Int,
    val name: String,
    val alsoKnownAs: Array<String>,
    val biography: String,
    val placeOfBirth: String?,
    val profilePath: String?,
    val imdbId: String?,
    val homepage: String?
): Parcelable
