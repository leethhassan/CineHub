package com.example.data.seed

import com.example.data.local.AppDatabase
import com.example.data.model.CastEntity
import com.example.data.model.EpisodeEntity
import com.example.data.model.GenreEntity
import com.example.data.model.MovieCastEntity
import com.example.data.model.MovieEntity
import com.example.data.model.MovieGenreEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.SeasonEntity
import com.example.data.model.SeriesCastEntity
import com.example.data.model.SeriesEntity
import com.example.data.model.SeriesGenreEntity
import com.example.data.model.UserEntity
import com.example.data.model.WatchHistoryEntity
import com.example.data.model.WatchlistEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CineHubDatabaseSeeder {

    suspend fun seedIfNeeded(database: AppDatabase) = withContext(Dispatchers.IO) {
        val userCount = database.userDao().getUsersCount()
        if (userCount > 0) return@withContext // Already seeded

        // 1. Seed Users (Admin + Demo User)
        val admin = UserEntity(
            name = "Admin Master",
            email = "admin@cinehub.com",
            passwordHash = "admin123", // In production hashed with bcrypt
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80",
            role = "admin",
            token = "sanctum_admin_token_cinehub_2024"
        )
        val demoUser = UserEntity(
            name = "Alex Vance",
            email = "user@cinehub.com",
            passwordHash = "user123",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=400&q=80",
            role = "user",
            token = "sanctum_user_token_cinehub_2024"
        )
        val adminId = database.userDao().insertUser(admin)
        val userId = database.userDao().insertUser(demoUser)

        // 2. Seed Genres
        val genres = listOf(
            GenreEntity(id = 1, name = "Sci-Fi"),
            GenreEntity(id = 2, name = "Action"),
            GenreEntity(id = 3, name = "Drama"),
            GenreEntity(id = 4, name = "Adventure"),
            GenreEntity(id = 5, name = "Animation"),
            GenreEntity(id = 6, name = "Cyberpunk"),
            GenreEntity(id = 7, name = "Thriller"),
            GenreEntity(id = 8, name = "Fantasy"),
            GenreEntity(id = 9, name = "Documentary")
        )
        genres.forEach { database.genreDao().insertGenre(it) }

        // 3. Open Movie Streams
        // Blender Open Movies / openly licensed content.
        val streamTears = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
        val streamBunny = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        val streamSintel = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
        val streamElephant = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"

        // 4. Seed Movies
        val m1 = MovieEntity(
            id = 1,
            title = "Big Buck Bunny",
            description = "A giant rabbit named Big Buck Bunny enjoys a peaceful day in the forest until three mischievous rodents disturb the peace.",
            poster = "https://commons.wikimedia.org/wiki/Special:Redirect/file/Big_buck_bunny_poster_big.jpg",
            backdrop = "https://commons.wikimedia.org/wiki/Special:Redirect/file/BBB-Bunny.png",
            trailerUrl = streamBunny,
            videoUrl = streamBunny,
            releaseDate = "2008",
            durationMinutes = 10,
            rating = 0.0f,
            ageRating = "ALL",
            language = "English",
            country = "Netherlands",
            isPublished = true,
            isTrending = false,
            isFeatured = true,
            viewsCount = 0
        )

        val m2 = MovieEntity(
            id = 2,
            title = "Sintel",
            description = "A young warrior named Sintel begins a dangerous journey to find a dragon she has grown attached to.",
            poster = "https://commons.wikimedia.org/wiki/Special:Redirect/file/Sintel_poster.jpg",
            backdrop = "https://commons.wikimedia.org/wiki/Special:Redirect/file/Sintel_1920x1080.png",
            trailerUrl = streamSintel,
            videoUrl = streamSintel,
            releaseDate = "2010",
            durationMinutes = 15,
            rating = 0.0f,
            ageRating = "PG",
            language = "English",
            country = "Netherlands",
            isPublished = true,
            isTrending = false,
            isFeatured = true,
            viewsCount = 0
        )

        val m3 = MovieEntity(
            id = 3,
            title = "Tears of Steel",
            description = "A group of warriors and scientists reunite in Amsterdam to save the world from destructive robots from the future.",
            poster = "https://commons.wikimedia.org/wiki/Special:Redirect/file/Tos-poster.png",
            backdrop = "https://commons.wikimedia.org/wiki/Special:Redirect/file/Tears_of_Steel_frame_03_2e.jpg",
            trailerUrl = streamTears,
            videoUrl = streamTears,
            releaseDate = "2012",
            durationMinutes = 12,
            rating = 0.0f,
            ageRating = "PG-13",
            language = "English",
            country = "Netherlands",
            isPublished = true,
            isTrending = false,
            isFeatured = false,
            viewsCount = 0
        )

        val m4 = MovieEntity(
            id = 4,
            title = "Elephants Dream",
            description = "Two characters explore a strange surreal world filled with mysterious machines and unexpected discoveries.",
            poster = "https://commons.wikimedia.org/wiki/Special:Redirect/file/ElephantsDreamPoster.jpg",
            backdrop = "https://commons.wikimedia.org/wiki/Special:Redirect/file/Elephants_Dream_-_Emo_and_Proog.png",
            trailerUrl = streamElephant,
            videoUrl = streamElephant,
            releaseDate = "2006",
            durationMinutes = 11,
            rating = 0.0f,
            ageRating = "PG",
            language = "English",
            country = "Netherlands",
            isPublished = true,
            isTrending = false,
            isFeatured = false,
            viewsCount = 0
        )

        listOf(m1, m2, m3, m4).forEach {
            database.movieDao().insertMovie(it)
        }

        // Movie genres
        database.genreDao().insertMovieGenre(MovieGenreEntity(1, 5))
        database.genreDao().insertMovieGenre(MovieGenreEntity(1, 4))

        database.genreDao().insertMovieGenre(MovieGenreEntity(2, 8))
        database.genreDao().insertMovieGenre(MovieGenreEntity(2, 5))

        database.genreDao().insertMovieGenre(MovieGenreEntity(3, 1))
        database.genreDao().insertMovieGenre(MovieGenreEntity(3, 2))
        database.genreDao().insertMovieGenre(MovieGenreEntity(3, 6))

        database.genreDao().insertMovieGenre(MovieGenreEntity(4, 1))
        database.genreDao().insertMovieGenre(MovieGenreEntity(4, 5))

        // No fake cast members.
        // No fake series, seasons, or episodes.

        // Demo user activity intentionally left empty.
        // Watch history, watchlist and notifications are created by real user actions.

    }
}
