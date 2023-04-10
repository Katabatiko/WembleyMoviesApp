package com.gonpas.wembleymoviesapp.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gonpas.wembleymoviesapp.domain.DomainMovie

@Entity
data class MovieDb constructor(
    @PrimaryKey
    val movieId: Int,
    val backdropPath: String?,
    val title: String,
    val overview: String,
    val imgUrl: String?,
    val popularity: Float,
    val releaseDate: String,
    val voteAverage: Float,
    val voteCount: Int
)

fun LiveData<List<MovieDb>>.asListDomainMovies(): LiveData<List<DomainMovie>>{
    return map { list ->
        list.map {
            DomainMovie(
                backdropPath = it.backdropPath,
                id = it.movieId,
                title = it.title,
                overview = it.overview,
                imgUrl = it.imgUrl,
                popularity = it.popularity,
                releaseDate = it.releaseDate,
                voteAverage = it.voteAverage,
                voteCount = it.voteCount,
                fav = true
            )
        }
    }
}