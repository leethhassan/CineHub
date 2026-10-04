package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.CastEntity
import com.example.data.model.EpisodeEntity
import com.example.data.model.GenreEntity
import com.example.data.model.MovieCastEntity
import com.example.data.model.MovieEntity
import com.example.data.model.MovieGenreEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.RatingEntity
import com.example.data.model.SeasonEntity
import com.example.data.model.SeriesCastEntity
import com.example.data.model.SeriesEntity
import com.example.data.model.SeriesGenreEntity
import com.example.data.model.UserEntity
import com.example.data.model.WatchHistoryEntity
import com.example.data.model.WatchlistEntity

@Database(
    entities = [
        UserEntity::class,
        MovieEntity::class,
        SeriesEntity::class,
        SeasonEntity::class,
        EpisodeEntity::class,
        GenreEntity::class,
        MovieGenreEntity::class,
        SeriesGenreEntity::class,
        CastEntity::class,
        MovieCastEntity::class,
        SeriesCastEntity::class,
        RatingEntity::class,
        WatchlistEntity::class,
        WatchHistoryEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun movieDao(): MovieDao
    abstract fun seriesDao(): SeriesDao
    abstract fun seasonDao(): SeasonDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun genreDao(): GenreDao
    abstract fun castDao(): CastDao
    abstract fun ratingDao(): RatingDao
    abstract fun watchlistDao(): WatchlistDao
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cinehub_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
