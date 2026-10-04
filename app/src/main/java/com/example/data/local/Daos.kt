package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE token IS NOT NULL LIMIT 1")
    suspend fun getActiveSessionUser(): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: Long)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUsersCount(): Int
}

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies WHERE isPublished = 1 ORDER BY id DESC")
    fun getAllMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    suspend fun getMovieById(id: Long): MovieEntity?

    @Query("SELECT * FROM movies WHERE isPublished = 1 AND isFeatured = 1 LIMIT 1")
    fun getFeaturedMovie(): Flow<MovieEntity?>

    @Query("SELECT * FROM movies WHERE isPublished = 1 AND isTrending = 1 ORDER BY rating DESC")
    fun getTrendingMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isPublished = 1 ORDER BY rating DESC LIMIT 10")
    fun getPopularMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isPublished = 1 ORDER BY releaseDate DESC LIMIT 10")
    fun getLatestMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isPublished = 1 ORDER BY rating DESC LIMIT 10")
    fun getTopRatedMovies(): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity): Long

    @Update
    suspend fun updateMovie(movie: MovieEntity)

    @Query("DELETE FROM movies WHERE id = :id")
    suspend fun deleteMovie(id: Long)

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMoviesCount(): Int

    @Query("UPDATE movies SET viewsCount = viewsCount + 1 WHERE id = :id")
    suspend fun incrementViews(id: Long)

    @Query("SELECT SUM(viewsCount) FROM movies")
    suspend fun getTotalViews(): Long?
}

@Dao
interface SeriesDao {
    @Query("SELECT * FROM series WHERE isPublished = 1 ORDER BY id DESC")
    fun getAllSeries(): Flow<List<SeriesEntity>>

    @Query("SELECT * FROM series WHERE id = :id LIMIT 1")
    suspend fun getSeriesById(id: Long): SeriesEntity?

    @Query("SELECT * FROM series WHERE isPublished = 1 AND isTrending = 1 ORDER BY rating DESC")
    fun getTrendingSeries(): Flow<List<SeriesEntity>>

    @Query("SELECT * FROM series WHERE isPublished = 1 ORDER BY rating DESC LIMIT 10")
    fun getPopularSeries(): Flow<List<SeriesEntity>>

    @Query("SELECT * FROM series WHERE isPublished = 1 ORDER BY releaseDate DESC LIMIT 10")
    fun getLatestSeries(): Flow<List<SeriesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeries(series: SeriesEntity): Long

    @Update
    suspend fun updateSeries(series: SeriesEntity)

    @Query("DELETE FROM series WHERE id = :id")
    suspend fun deleteSeries(id: Long)

    @Query("SELECT COUNT(*) FROM series")
    suspend fun getSeriesCount(): Int

    @Query("UPDATE series SET viewsCount = viewsCount + 1 WHERE id = :id")
    suspend fun incrementViews(id: Long)
}

@Dao
interface SeasonDao {
    @Query("SELECT * FROM seasons WHERE seriesId = :seriesId ORDER BY seasonNumber ASC")
    fun getSeasonsForSeries(seriesId: Long): Flow<List<SeasonEntity>>

    @Query("SELECT * FROM seasons WHERE seriesId = :seriesId ORDER BY seasonNumber ASC")
    suspend fun getSeasonsListForSeries(seriesId: Long): List<SeasonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeason(season: SeasonEntity): Long

    @Query("DELETE FROM seasons WHERE id = :id")
    suspend fun deleteSeason(id: Long)

    @Query("SELECT COUNT(*) FROM seasons")
    suspend fun getSeasonsCount(): Int
}

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes WHERE seasonId = :seasonId ORDER BY episodeNumber ASC")
    fun getEpisodesForSeason(seasonId: Long): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM episodes WHERE seasonId = :seasonId ORDER BY episodeNumber ASC")
    suspend fun getEpisodesListForSeason(seasonId: Long): List<EpisodeEntity>

    @Query("SELECT * FROM episodes WHERE seriesId = :seriesId ORDER BY seasonId ASC, episodeNumber ASC")
    fun getEpisodesForSeries(seriesId: Long): Flow<List<EpisodeEntity>>

    @Query("SELECT * FROM episodes WHERE id = :id LIMIT 1")
    suspend fun getEpisodeById(id: Long): EpisodeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: EpisodeEntity): Long

    @Update
    suspend fun updateEpisode(episode: EpisodeEntity)

    @Query("DELETE FROM episodes WHERE id = :id")
    suspend fun deleteEpisode(id: Long)

    @Query("SELECT COUNT(*) FROM episodes")
    suspend fun getEpisodesCount(): Int
}

@Dao
interface GenreDao {
    @Query("SELECT * FROM genres ORDER BY name ASC")
    fun getAllGenres(): Flow<List<GenreEntity>>

    @Query("SELECT * FROM genres ORDER BY name ASC")
    suspend fun getAllGenresList(): List<GenreEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenre(genre: GenreEntity): Long

    @Query("""
        SELECT g.* FROM genres g
        INNER JOIN movie_genres mg ON g.id = mg.genreId
        WHERE mg.movieId = :movieId
    """)
    fun getGenresForMovie(movieId: Long): Flow<List<GenreEntity>>

    @Query("""
        SELECT g.* FROM genres g
        INNER JOIN movie_genres mg ON g.id = mg.genreId
        WHERE mg.movieId = :movieId
    """)
    suspend fun getGenresListForMovie(movieId: Long): List<GenreEntity>

    @Query("""
        SELECT g.* FROM genres g
        INNER JOIN series_genres sg ON g.id = sg.genreId
        WHERE sg.seriesId = :seriesId
    """)
    fun getGenresForSeries(seriesId: Long): Flow<List<GenreEntity>>

    @Query("""
        SELECT g.* FROM genres g
        INNER JOIN series_genres sg ON g.id = sg.genreId
        WHERE sg.seriesId = :seriesId
    """)
    suspend fun getGenresListForSeries(seriesId: Long): List<GenreEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovieGenre(mg: MovieGenreEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSeriesGenre(sg: SeriesGenreEntity)

    @Query("DELETE FROM movie_genres WHERE movieId = :movieId")
    suspend fun deleteMovieGenres(movieId: Long)
}

@Dao
interface CastDao {
    @Query("""
        SELECT c.* FROM casts c
        INNER JOIN movie_casts mc ON c.id = mc.castId
        WHERE mc.movieId = :movieId
    """)
    fun getCastForMovie(movieId: Long): Flow<List<CastEntity>>

    @Query("""
        SELECT c.* FROM casts c
        INNER JOIN movie_casts mc ON c.id = mc.castId
        WHERE mc.movieId = :movieId
    """)
    suspend fun getCastListForMovie(movieId: Long): List<CastEntity>

    @Query("""
        SELECT c.* FROM casts c
        INNER JOIN series_casts sc ON c.id = sc.castId
        WHERE sc.seriesId = :seriesId
    """)
    fun getCastForSeries(seriesId: Long): Flow<List<CastEntity>>

    @Query("""
        SELECT c.* FROM casts c
        INNER JOIN series_casts sc ON c.id = sc.castId
        WHERE sc.seriesId = :seriesId
    """)
    suspend fun getCastListForSeries(seriesId: Long): List<CastEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCast(cast: CastEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovieCast(mc: MovieCastEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSeriesCast(sc: SeriesCastEntity)
}

@Dao
interface RatingDao {
    @Query("SELECT * FROM ratings WHERE contentId = :contentId AND contentType = :contentType ORDER BY createdAt DESC")
    fun getRatingsForContent(contentId: Long, contentType: String): Flow<List<RatingEntity>>

    @Query("SELECT * FROM ratings WHERE userId = :userId AND contentId = :contentId AND contentType = :contentType LIMIT 1")
    fun getUserRating(userId: Long, contentId: Long, contentType: String): Flow<RatingEntity?>

    @Query("SELECT * FROM ratings WHERE userId = :userId AND contentId = :contentId AND contentType = :contentType LIMIT 1")
    suspend fun getUserRatingDirect(userId: Long, contentId: Long, contentType: String): RatingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRating(rating: RatingEntity)

    @Query("DELETE FROM ratings WHERE userId = :userId AND contentId = :contentId AND contentType = :contentType")
    suspend fun deleteRating(userId: Long, contentId: Long, contentType: String)

    @Query("SELECT COUNT(*) FROM ratings")
    suspend fun getRatingsCount(): Int

    @Query("SELECT * FROM ratings WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserRatings(userId: Long): Flow<List<RatingEntity>>
}

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlists WHERE userId = :userId ORDER BY addedAt DESC")
    fun getUserWatchlist(userId: Long): Flow<List<WatchlistEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlists WHERE userId = :userId AND contentId = :contentId AND contentType = :contentType)")
    fun isInWatchlist(userId: Long, contentId: Long, contentType: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlists WHERE userId = :userId AND contentId = :contentId AND contentType = :contentType)")
    suspend fun isInWatchlistDirect(userId: Long, contentId: Long, contentType: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWatchlist(item: WatchlistEntity): Long

    @Query("DELETE FROM watchlists WHERE userId = :userId AND contentId = :contentId AND contentType = :contentType")
    suspend fun removeFromWatchlist(userId: Long, contentId: Long, contentType: String)

    @Query("DELETE FROM watchlists WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface WatchHistoryDao {
    @Query("SELECT * FROM watch_histories WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getUserWatchHistory(userId: Long): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_histories WHERE userId = :userId AND contentId = :contentId AND contentType = :contentType LIMIT 1")
    fun getWatchProgress(userId: Long, contentId: Long, contentType: String): Flow<WatchHistoryEntity?>

    @Query("SELECT * FROM watch_histories WHERE userId = :userId AND contentId = :contentId AND contentType = :contentType LIMIT 1")
    suspend fun getWatchProgressDirect(userId: Long, contentId: Long, contentType: String): WatchHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(history: WatchHistoryEntity)

    @Query("DELETE FROM watch_histories WHERE userId = :userId AND contentId = :contentId AND contentType = :contentType")
    suspend fun deleteProgress(userId: Long, contentId: Long, contentType: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId OR userId = 0 ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: Long): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId OR userId = 0")
    suspend fun markAllAsRead(userId: Long)

    @Query("SELECT COUNT(*) FROM notifications WHERE (userId = :userId OR userId = 0) AND isRead = 0")
    fun getUnreadCount(userId: Long): Flow<Int>
}
