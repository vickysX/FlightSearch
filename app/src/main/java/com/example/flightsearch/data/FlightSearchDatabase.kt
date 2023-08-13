package com.example.flightsearch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flightsearch.data.daos.AirportDao
import com.example.flightsearch.data.daos.FavoriteDao
import com.example.flightsearch.models.Airport
import com.example.flightsearch.models.Favorite

@Database(entities = [Airport::class, Favorite::class], version = 1)
abstract class FlightSearchDatabase : RoomDatabase() {

    abstract fun airportDao() : AirportDao
    abstract fun favoriteDao() : FavoriteDao

    companion object {
        @Volatile
        private var instance: FlightSearchDatabase? = null

        fun getDatabase(context: Context) : FlightSearchDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context, FlightSearchDatabase::class.java, "flight_search.db"
                )
                    .createFromAsset("database/flight_search.db")
                    .fallbackToDestructiveMigration()
                    .build().also {
                        instance = it
                    }
            }
        }
    }

}