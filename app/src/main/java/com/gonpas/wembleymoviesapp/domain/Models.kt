package com.gonpas.wembleymoviesapp.domain

import android.os.Parcelable
import com.gonpas.wembleymoviesapp.database.MovieDb
import com.gonpas.wembleymoviesapp.utils.smartTruncate
import kotlinx.parcelize.Parcelize
/**
 * Los domain objects son los objetos que maneja kotlin y que serán los que
 * manejen la app y se muestren en la pantalla
 */
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
    val fav: Boolean = false
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
