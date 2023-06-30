package com.gonpas.wembleymoviesapp.database

object StubLocalData {
    val movieDb1 = MovieDb(
        movieId = 100,
        title = "El test del algoritmo",
        overview = "Probando las funcionalidades de Room.",
        releaseDate = "2023-05-17",
        backdropPath = null,
        imgUrl = "http://image.tmdb.org/t/p/w185/poster.jpg",
        popularity = 7.7f,
        voteAverage = 8.8f,
        voteCount = 1000
    )
    val movieDb2 = MovieDb(
        backdropPath = null,
        movieId = 10002,
        title = "Vientos del norte",
        overview = "Es la historia de Boreal",
        imgUrl = "http://image.tmdb.org/t/p/w185/poster.jpg",
        releaseDate = "2023-3-3",
        popularity = 6.5f,
        voteAverage = 8.8f,
        voteCount = 535
    )
    val movieDb3 = MovieDb(
        backdropPath = "/3CxUndGhUcZdt1Zggjdb2HkLLQX.jpg",
        movieId = 640146,
        title = "Ant-Man y la Avispa: Quantumanía",
        overview = "La pareja de superhéroes Scott Lang y Hope van Dyne regresa para continuar sus aventuras",
        imgUrl = "http://image.tmdb.org/t/p/w185/jTNYlTEijZ6c8Mn4gvINOeB2HWM.jpg",
        releaseDate = "2023-02-15",
        popularity = 4665.438f,
        voteAverage = 6.573f,
        voteCount = 2301
    )
}