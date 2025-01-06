package com.example.market.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.market.model.Listing

@Database(
    entities = [Listing::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(CategoryConverter::class, ConditionConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun listingDao(): ListingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "market_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}