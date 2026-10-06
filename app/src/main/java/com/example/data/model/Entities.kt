package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val avatarUrl: String = "",
    val role: String = "user", // "user" or "admin"
    val token: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val poster: String,
    val backdrop: String,
    val trailerUrl: String,
    val videoUrl: String,
    val releaseDate: String, // e.g. "2024"
    val durationMinutes: Int,
    val rating: Float,
    val ageRating: String = "16+",
    val language: String = "English",
    val country: String = "USA",
    val isPublished: Boolean = true,
    val isTrending: Boolean = false,
    val isFeatured: Boolean = false,
    val viewsCount: Long = 0
)

@Entity(tableName = "series")
data class SeriesEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val poster: String,
    val backdrop: String,
    val trailerUrl: String,
    val releaseDate: String,
    val rating: Float,
    val language: String = "English",
    val country: String = "USA",
    val isPublished: Boolean = true,
    val isTrending: Boolean = false,
    val isFeatured: Boolean = false,
    val viewsCount: Long = 0
)

@Entity(
    tableName = "seasons",
    indices = [Index(value = ["seriesId"])]
)
data class SeasonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val seriesId: Long,
    val seasonNumber: Int,
    val title: String,
    val releaseDate: String
)

@Entity(
    tableName = "episodes",
    indices = [Index(value = ["seasonId"]), Index(value = ["seriesId"])]
)
data class EpisodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val seasonId: Long,
    val seriesId: Long,
    val episodeNumber: Int,
    val title: String,
    val description: String,
    val durationMinutes: Int,
    val thumbnail: String,
    val videoUrl: String,
    val releaseDate: String
)

@Entity(tableName = "genres")
data class GenreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

@Entity(
    tableName = "movie_genres",
    primaryKeys = ["movieId", "genreId"],
    indices = [Index(value = ["genreId"])]
)
data class MovieGenreEntity(
    val movieId: Long,
    val genreId: Long
)

@Entity(
    tableName = "series_genres",
    primaryKeys = ["seriesId", "genreId"],
    indices = [Index(value = ["genreId"])]
)
data class SeriesGenreEntity(
    val seriesId: Long,
    val genreId: Long
)

@Entity(tableName = "casts")
data class CastEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val characterName: String,
    val avatarUrl: String,
    val role: String = "Actor"
)

@Entity(
    tableName = "movie_casts",
    primaryKeys = ["movieId", "castId"],
    indices = [Index(value = ["castId"])]
)
data class MovieCastEntity(
    val movieId: Long,
    val castId: Long
)

@Entity(
    tableName = "series_casts",
    primaryKeys = ["seriesId", "castId"],
    indices = [Index(value = ["castId"])]
)
data class SeriesCastEntity(
    val seriesId: Long,
    val castId: Long
)

@Entity(
    tableName = "ratings",
    indices = [Index(value = ["userId", "contentId", "contentType"], unique = true)]
)
data class RatingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val contentId: Long,
    val contentType: String, // "movie" or "series"
    val rating: Int, // 1 to 5
    val review: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "watchlists",
    indices = [Index(value = ["userId", "contentId", "contentType"], unique = true)]
)
data class WatchlistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val contentId: Long,
    val contentType: String, // "movie" or "series"
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "watch_histories",
    indices = [Index(value = ["userId", "contentId", "contentType"], unique = true)]
)
data class WatchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val contentId: Long,
    val contentType: String, // "movie" or "series"
    val episodeId: Long? = null,
    val progressSeconds: Long = 0,
    val durationSeconds: Long = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long, // 0 for global/system
    val title: String,
    val message: String,
    val type: String = "system", // "movie", "series", "episode", "system", "recommendation"
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val actionContentId: Long? = null,
    val actionContentType: String? = null
)
