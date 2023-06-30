package com.gonpas.wembleymoviesapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MovieDb::class], version = 1)
abstract class MoviesDatabase : RoomDatabase(){
    abstract val movieDao: MoviesDao
}

/*
internal lateinit var INSTANCE: MoviesDatabase

fun getDatabase(context: Context): MoviesDatabase {
    if (!::INSTANCE.isInitialized){
        INSTANCE = Room.databaseBuilder(
            context.applicationContext,
            MoviesDatabase::class.java,
            "movies"
        ).build()
    }
    return INSTANCE
}*/
