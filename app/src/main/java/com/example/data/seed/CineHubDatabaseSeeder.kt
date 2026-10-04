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

        // 3. Seed Cast Members
        val casts = listOf(
            CastEntity(1, "David K.", "Capt. Nolan Vance", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=300&q=80", "Lead Actor"),
            CastEntity(2, "Elena Rostova", "Dr. Lyra Thorne", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=300&q=80", "Lead Actress"),
            CastEntity(3, "Marcus Kane", "Commander Jax", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=300&q=80", "Supporting Actor"),
            CastEntity(4, "Sarah Lin", "Cipher / AI Voice", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=300&q=80", "Supporting Actress"),
            CastEntity(5, "Kenji Sato", "Renegade Pilot", "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?auto=format&fit=crop&w=300&q=80", "Supporting Actor")
        )
        casts.forEach { database.castDao().insertCast(it) }

        // Sample legal video streams (Creative Commons / Open movie test streams)
        val streamTears = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
        val streamBunny = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        val streamSintel = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
        val streamElephant = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
        val streamSubaru = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"

        // 4. Seed Movies
        val m1 = MovieEntity(
            id = 1,
            title = "Tears of Steel: Cyber Horizon",
            description = "In a dystopian cyberpunk future in Amsterdam, a team of freedom fighters and tech sorcerers struggle to rescue humanity from rogue cybernetic sentinels.",
            poster = "https://images.unsplash.com/photo-1578632767115-351597cf2477?auto=format&fit=crop&w=600&q=80",
            backdrop = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=1200&q=80",
            trailerUrl = streamTears,
            videoUrl = streamTears,
            releaseDate = "2024",
            durationMinutes = 118,
            rating = 4.9f,
            ageRating = "16+",
            language = "English / Dutch",
            country = "Netherlands / USA",
            isPublished = true,
            isTrending = true,
            isFeatured = true,
            viewsCount = 12450
        )

        val m2 = MovieEntity(
            id = 2,
            title = "Nebula Odyssey",
            description = "A deep-space surveyor discovers an enigmatic ancient derelict station pulsating with alien quantum signatures on the edge of the galaxy.",
            poster = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=600&q=80",
            backdrop = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1200&q=80",
            trailerUrl = streamSubaru,
            videoUrl = streamSubaru,
            releaseDate = "2024",
            durationMinutes = 134,
            rating = 4.7f,
            ageRating = "13+",
            language = "English",
            country = "USA",
            isPublished = true,
            isTrending = true,
            isFeatured = false,
            viewsCount = 9820
        )

        val m3 = MovieEntity(
            id = 3,
            title = "Sintel: Dragon Quest",
            description = "A solitary warrior girl named Sintel embarks on an arduous quest across arid deserts and snow peaks to find her companion baby dragon Scales.",
            poster = "https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&w=600&q=80",
            backdrop = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=1200&q=80",
            trailerUrl = streamSintel,
            videoUrl = streamSintel,
            releaseDate = "2023",
            durationMinutes = 95,
            rating = 4.8f,
            ageRating = "PG",
            language = "English",
            country = "Netherlands",
            isPublished = true,
            isTrending = false,
            isFeatured = false,
            viewsCount = 14200
        )

        val m4 = MovieEntity(
            id = 4,
            title = "Forest of Whispers: Bunny Tale",
            description = "An entertaining tale of a giant gentle forest bunny confronting mischievous forest creatures in an exhilarating adventure.",
            poster = "https://images.unsplash.com/photo-1535498730771-e735b998cd64?auto=format&fit=crop&w=600&q=80",
            backdrop = "https://images.unsplash.com/photo-1448375240586-882707db888b?auto=format&fit=crop&w=1200&q=80",
            trailerUrl = streamBunny,
            videoUrl = streamBunny,
            releaseDate = "2023",
            durationMinutes = 88,
            rating = 4.6f,
            ageRating = "ALL",
            language = "English",
            country = "Global",
            isPublished = true,
            isTrending = true,
            isFeatured = false,
            viewsCount = 18900
        )

        val m5 = MovieEntity(
            id = 5,
            title = "Shadow Protocol: Rogue Sector",
            description = "Undercover operatives infiltrate an illegal high-tech black market weapons syndicate operating in the sub-levels of Neo-Tokyo.",
            poster = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=600&q=80",
            backdrop = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?auto=format&fit=crop&w=1200&q=80",
            trailerUrl = streamElephant,
            videoUrl = streamElephant,
            releaseDate = "2024",
            durationMinutes = 112,
            rating = 4.5f,
            ageRating = "18+",
            language = "English / Japanese",
            country = "Japan / UK",
            isPublished = true,
            isTrending = false,
            isFeatured = false,
            viewsCount = 7600
        )

        listOf(m1, m2, m3, m4, m5).forEach { database.movieDao().insertMovie(it) }

        // Associate Movie Genres
        database.genreDao().insertMovieGenre(MovieGenreEntity(1, 1)) // Sci-Fi
        database.genreDao().insertMovieGenre(MovieGenreEntity(1, 2)) // Action
        database.genreDao().insertMovieGenre(MovieGenreEntity(1, 6)) // Cyberpunk

        database.genreDao().insertMovieGenre(MovieGenreEntity(2, 1)) // Sci-Fi
        database.genreDao().insertMovieGenre(MovieGenreEntity(2, 4)) // Adventure

        database.genreDao().insertMovieGenre(MovieGenreEntity(3, 8)) // Fantasy
        database.genreDao().insertMovieGenre(MovieGenreEntity(3, 3)) // Drama
        database.genreDao().insertMovieGenre(MovieGenreEntity(3, 5)) // Animation

        database.genreDao().insertMovieGenre(MovieGenreEntity(4, 5)) // Animation
        database.genreDao().insertMovieGenre(MovieGenreEntity(4, 4)) // Adventure

        database.genreDao().insertMovieGenre(MovieGenreEntity(5, 2)) // Action
        database.genreDao().insertMovieGenre(MovieGenreEntity(5, 7)) // Thriller

        // Associate Movie Casts
        database.castDao().insertMovieCast(MovieCastEntity(1, 1))
        database.castDao().insertMovieCast(MovieCastEntity(1, 2))
        database.castDao().insertMovieCast(MovieCastEntity(1, 3))

        database.castDao().insertMovieCast(MovieCastEntity(2, 1))
        database.castDao().insertMovieCast(MovieCastEntity(2, 4))

        database.castDao().insertMovieCast(MovieCastEntity(3, 2))
        database.castDao().insertMovieCast(MovieCastEntity(3, 4))

        database.castDao().insertMovieCast(MovieCastEntity(5, 3))
        database.castDao().insertMovieCast(MovieCastEntity(5, 5))

        // 5. Seed Series
        val s1 = SeriesEntity(
            id = 1,
            title = "Chronicles of the Wasteland",
            description = "A gritty post-apocalyptic saga following survivors navigating electromagnetic storms and warring factions to discover an underground sanctuary.",
            poster = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=600&q=80",
            backdrop = "https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&w=1200&q=80",
            trailerUrl = streamTears,
            releaseDate = "2024",
            rating = 4.9f,
            language = "English",
            country = "USA",
            isPublished = true,
            isTrending = true,
            isFeatured = true,
            viewsCount = 21000
        )

        val s2 = SeriesEntity(
            id = 2,
            title = "Cyber City: Neon Underground",
            description = "In an overcrowded megacity where mega-corporations rule the clouds, a rebellious network of hackers and street medics fight for the forgotten populace.",
            poster = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=600&q=80",
            backdrop = "https://images.unsplash.com/photo-1578632767115-351597cf2477?auto=format&fit=crop&w=1200&q=80",
            trailerUrl = streamSubaru,
            releaseDate = "2023",
            rating = 4.8f,
            language = "English",
            country = "Canada",
            isPublished = true,
            isTrending = true,
            isFeatured = false,
            viewsCount = 16400
        )

        val s3 = SeriesEntity(
            id = 3,
            title = "Deep Abyss: Oceanic Frontiers",
            description = "A high-stakes marine documentary exploring unexplored Mariana oceanic trenches and strange luminescent biosystems.",
            poster = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?auto=format&fit=crop&w=600&q=80",
            backdrop = "https://images.unsplash.com/photo-1518837695005-2083093ee35b?auto=format&fit=crop&w=1200&q=80",
            trailerUrl = streamElephant,
            releaseDate = "2024",
            rating = 4.7f,
            language = "English",
            country = "UK",
            isPublished = true,
            isTrending = false,
            isFeatured = false,
            viewsCount = 11200
        )

        listOf(s1, s2, s3).forEach { database.seriesDao().insertSeries(it) }

        // Associate Series Genres
        database.genreDao().insertSeriesGenre(SeriesGenreEntity(1, 1))
        database.genreDao().insertSeriesGenre(SeriesGenreEntity(1, 3))
        database.genreDao().insertSeriesGenre(SeriesGenreEntity(1, 7))

        database.genreDao().insertSeriesGenre(SeriesGenreEntity(2, 6))
        database.genreDao().insertSeriesGenre(SeriesGenreEntity(2, 1))
        database.genreDao().insertSeriesGenre(SeriesGenreEntity(2, 2))

        database.genreDao().insertSeriesGenre(SeriesGenreEntity(3, 9))
        database.genreDao().insertSeriesGenre(SeriesGenreEntity(3, 4))

        // Associate Series Cast
        database.castDao().insertSeriesCast(SeriesCastEntity(1, 1))
        database.castDao().insertSeriesCast(SeriesCastEntity(1, 2))
        database.castDao().insertSeriesCast(SeriesCastEntity(2, 3))
        database.castDao().insertSeriesCast(SeriesCastEntity(2, 4))

        // 6. Seed Seasons & Episodes for Series 1
        val season1 = SeasonEntity(id = 1, seriesId = 1, seasonNumber = 1, title = "Season 1: Fallout", releaseDate = "2024")
        val season2 = SeasonEntity(id = 2, seriesId = 1, seasonNumber = 2, title = "Season 2: Rebirth", releaseDate = "2024")
        database.seasonDao().insertSeason(season1)
        database.seasonDao().insertSeason(season2)

        val ep1 = EpisodeEntity(
            id = 1,
            seasonId = 1,
            seriesId = 1,
            episodeNumber = 1,
            title = "Episode 1: The Ash Awakening",
            description = "Nolan awakens in an abandoned fallout bunker only to find strange electromagnetic signals calling from the north.",
            durationMinutes = 48,
            thumbnail = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=500&q=80",
            videoUrl = streamTears,
            releaseDate = "2024-01-10"
        )
        val ep2 = EpisodeEntity(
            id = 2,
            seasonId = 1,
            seriesId = 1,
            episodeNumber = 2,
            title = "Episode 2: Sandstorm Syndicate",
            description = "Traversing the jagged dunes, the convoy encounters scavenger drones guarding a forgotten geothermal reactor.",
            durationMinutes = 52,
            thumbnail = "https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&w=500&q=80",
            videoUrl = streamSubaru,
            releaseDate = "2024-01-17"
        )
        val ep3 = EpisodeEntity(
            id = 3,
            seasonId = 1,
            seriesId = 1,
            episodeNumber = 3,
            title = "Episode 3: The Quantum Citadel",
            description = "Inside the hidden citadel, secrets of the cataclysm are finally unraveled by Dr. Thorne.",
            durationMinutes = 56,
            thumbnail = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=500&q=80",
            videoUrl = streamElephant,
            releaseDate = "2024-01-24"
        )
        listOf(ep1, ep2, ep3).forEach { database.episodeDao().insertEpisode(it) }

        // 7. Seed Continue Watching for Demo User
        database.watchHistoryDao().insertOrUpdateProgress(
            WatchHistoryEntity(
                userId = userId,
                contentId = 1,
                contentType = "movie",
                episodeId = null,
                progressSeconds = 2450,
                durationSeconds = 7080,
                updatedAt = System.currentTimeMillis() - 3600000
            )
        )
        database.watchHistoryDao().insertOrUpdateProgress(
            WatchHistoryEntity(
                userId = userId,
                contentId = 1,
                contentType = "series",
                episodeId = 1,
                progressSeconds = 1200,
                durationSeconds = 2880,
                updatedAt = System.currentTimeMillis() - 7200000
            )
        )

        // 8. Seed Watchlist for Demo User
        database.watchlistDao().addToWatchlist(
            WatchlistEntity(userId = userId, contentId = 2, contentType = "movie", addedAt = System.currentTimeMillis() - 100000)
        )
        database.watchlistDao().addToWatchlist(
            WatchlistEntity(userId = userId, contentId = 1, contentType = "series", addedAt = System.currentTimeMillis() - 200000)
        )

        // 9. Seed Notifications
        val notifs = listOf(
            NotificationEntity(
                userId = userId,
                title = "Welcome to CineHub Cinema",
                message = "Experience high-definition movies and series with seamless video playback, watchlists, and offline synchronization.",
                type = "system",
                isRead = false,
                actionContentId = 1,
                actionContentType = "movie"
            ),
            NotificationEntity(
                userId = userId,
                title = "New Blockbuster Added",
                message = "Tears of Steel: Cyber Horizon is now streaming in Ultra HD with Dolby Atmos audio support.",
                type = "movie",
                isRead = false,
                actionContentId = 1,
                actionContentType = "movie"
            ),
            NotificationEntity(
                userId = userId,
                title = "Continue Watching",
                message = "You have 78 minutes left in Tears of Steel. Resume where you left off!",
                type = "recommendation",
                isRead = true,
                actionContentId = 1,
                actionContentType = "movie"
            )
        )
        notifs.forEach { database.notificationDao().insertNotification(it) }
    }
}
