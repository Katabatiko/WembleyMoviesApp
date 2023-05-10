package com.gonpas.wembleymoviesapp.network

import kotlinx.coroutines.delay

object FakeAndroidTestRemoteService: TmdbApiService {

    private const val SERVICE_LATENCY = 0L

    private val imagesDto: ImagesDto
    private val configuration: Configuration
    private val results: Array<MovieDto>
    var actualMoviesDto: MoviesListDto




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
    val movieDto4 = MovieDto(
        adult = false,
        backdropPath = "/3CxUndGhUcZdt1Zggjdb2HkLLQX.jpg",
        genre_ids = arrayOf(
            28,
            12,
            878
        ),
        id = 640146,
        originalLanguage = "en",
        originalTitle = "Ant-Man and the Wasp: Quantumania",
        overview = "La pareja de superhéroes Scott Lang y Hope van Dyne regresa para continuar sus aventuras",
        popularity = 4665.438f,
        posterPath = "/jTNYlTEijZ6c8Mn4gvINOeB2HWM.jpg",
        releaseDate = "2023-02-15",
        title = "Ant-Man y la Avispa: Quantumanía",
        video = false,
        voteAverage = 6.573f,
        voteCount = 2301
    )

    private val evangeline = PersonDto(
        adult = false,
        alsoKnownAs = arrayOf(
            "에반젤린 릴리",
            "Εβάντζελιν Λίλι",
            "Nicole Evangeline Lilly",
            " エヴァンジェリン・リリー",
            "Еванджелін Ліллі"
        ),
        biography = "Nicole Evangeline Lilly (3 de agosto de 1979),​ conocida artísticamente como Evangeline Lilly, es una actriz y escritora canadiense. Logró popularidad por su papel de Kate Austen en la serie de televisión Lost (2004-2010), por la que obtuvo un Premio del Sindicato de Actores y recibió una nominación al Globo de Oro.​ También es reconocida por sus interpretaciones de Connie James en The Hurt Locker (2008), Hope Van Dyne en Ant-Man and the Wasp (2018), Bailey Tallet en Real Steel (2011) y Tauriel en la serie fílmica de El Hobbit.",
        birthday = "1979-08-03",
        deathday = null,
        gender = 1,
        homepage = "https://evangeline-lilly.com/",
        id = 19034,
        imdbId = "nm1431940",
        knowForDepartment = "Acting",
        name = "Evangeline Lilly",
        placeOfBirth = "Fort Saskatchewan, Alberta, Canada",
        popularity = 39.19f,
        profilePath = "/fRbXVt9fhz6ndPhF1lRA92VxUDk.jpg"
    )
    private val nobody = PersonDto(
        adult = false,
        alsoKnownAs = arrayOf(
            "Fulano",
            "Mengano",
            "Zutano"
        ),
        biography = "Tedio nacio en Madrid (España) en los años 60 del paado siglo",
        birthday = "1964-02-19",
        deathday = null,
        gender = 1,
        homepage = null,
        id = 0,
        imdbId = null,
        knowForDepartment = "Acting",
        name = "Tedio Plomez Sopor",
        placeOfBirth = "Madrid, España",
        popularity = 99.99f,
        profilePath = null
    )

    val creditsDto = CreditsDto(
        id = 640146,
        cast = arrayOf(
            ActoresDto(
                adult = false,
                gender = 2,
                id = 22226,
                knownForDepartment = "Acting",
                name = "Paul Rudd",
                originalName = "Paul Rudd",
                popularity = 42.975f,
                profilePath = "/8eTtJ7XVXY0BnEeUaSiTAraTIXd.jpg",
                castId = 1,
                character = "Scott Lang / Ant-Man",
                creditId = "5da88f43a2423200177bf176",
                order = 0
            ),
            ActoresDto(
                adult =  false,
                gender = 1,
                id = 19034,
                knownForDepartment = "Acting",
                name = "Evangeline Lilly",
                originalName = "Evangeline Lilly",
                popularity = 39.19f,
                profilePath = "/fRbXVt9fhz6ndPhF1lRA92VxUDk.jpg",
                castId = 2,
                character = "Hope van Dyne / The Wasp",
                creditId = "5da88f59944a570019264e0e",
                order = 1
            ),
            ActoresDto(
                adult =  false,
                gender = 1,
                id = 1160,
                knownForDepartment = "Acting",
                name = "Michelle Pfeiffer",
                originalName = "Michelle Pfeiffer",
                popularity = 39.662f,
                profilePath = "/oGUmQBU87QXAsnaGleYaAjAXSlj.jpg",
                castId = 15,
                character = "Janet van Dyne",
                creditId = "5dbf376defe37c001383ed0d",
                order = 4
            ),
            ActoresDto(
                adult =  false,
                gender = 2,
                id = 3392,
                knownForDepartment = "Acting",
                name = "Michael Douglas",
                originalName = "Michael Douglas",
                popularity = 14.929f,
                profilePath = "/kVYGPIZowzXLEQfAGUNOqKjAbBb.jpg",
                castId = 16,
                character = "Dr. Hank Pym",
                creditId = "5dbf3786f1b5710010e7221c",
                order = 5
            )
        ),
        crew = arrayOf(
            CrewDto(
                adult = false,
                gender = 2,
                id = 7624,
                knownForDepartment = "Writing",
                name = "Stan Lee",
                originalName = "Stan Lee",
                popularity = 18.066f,
                profilePath = "/kKeyWoFtTqOPsbmwylNHmuB3En9.jpg",
                creditId = "5da88fdbe6d3cc0018b04e35",
                department = "Writing",
                job = "Characters"
            ),
            CrewDto(
                adult = false,
                gender = 2,
                id = 9341,
                knownForDepartment = "Camera",
                name = "Bill Pope",
                originalName = "Bill Pope",
                popularity = 1.679f,
                profilePath = "/kpakvuSrk1D9D8WMt5SOi4Rs2EV.jpg",
                creditId = "6289717ae004a66f32b15131",
                department = "Camera",
                job = "Director of Photography"
            ),
            CrewDto(
                adult = false,
                gender = 2,
                id = 59026,
                knownForDepartment = "Directing",
                name = "Peyton Reed",
                originalName = "Peyton Reed",
                popularity = 6.278f,
                profilePath = "/aucXNycAtFREMIKr6ikmHvd2Mmp.jpg",
                creditId = "5dbc9866d388ae0016d85970",
                department = "Directing",
                job = "Director"
            ),
            CrewDto(
                adult = false,
                gender = 2,
                id = 18866,
                knownForDepartment = "Writing",
                name = "Jack Kirby",
                originalName = "Jack Kirby",
                popularity = 4.382f,
                profilePath = "/ihhR019gL1WrXdSQNJITAY6dont.jpg",
                creditId = "5dbd321c9638640016e10f30",
                department = "Writing",
                job = "Characters"
            ),
            CrewDto(
                adult = false,
                gender = 2,
                id = 23486,
                knownForDepartment = "Sound",
                name = "Christophe Beck",
                originalName = "Christophe Beck",
                popularity = 1.971f,
                profilePath = "/n3U0OPETTZGTx4mgsrryARKphR4.jpg",
                creditId = "62ca36d1d705940051fd2ff4",
                department = "Sound",
                job = "Original Music Composer"
            )
        )
    )
    val creditsDtoEmpty = CreditsDto(
        id = 0,
        cast = arrayOf<ActoresDto>(),
        crew = arrayOf<CrewDto>()
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

        results = arrayOf(movieDto1, movieDto3, movieDto4, movieDto2)
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

    override suspend fun getMovieCredits(
        movie_id: Int,
        api_key: String,
        language: String
    ): CreditsDto {
        delay(SERVICE_LATENCY)
        return  if (movie_id == 640146)     creditsDto
        else                                creditsDtoEmpty
    }

    override suspend fun getPerson(person_id: Int, api_key: String, language: String): PersonDto {
        delay(SERVICE_LATENCY)
        return  if (person_id == 19034) evangeline
        else                            nobody
    }
}