package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.remote.*
import com.example.util.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CineHubRepository(
    private val context: Context,
    private val database: AppDatabase,
    private val scope: CoroutineScope
) {
    private val _currentSession = MutableStateFlow<AuthSession>(AuthSession())
    val currentSession: StateFlow<AuthSession> = _currentSession.asStateFlow()

    private val api: CineHubApiService
        get() = ApiClient.getService()

    init {
        // Restore authenticated user session dynamically from token cache
        scope.launch(Dispatchers.IO) {
            val user = database.userDao().getActiveSessionUser()
            if (user != null && user.token != null) {
                ApiClient.setAuthToken(user.token)
                _currentSession.value = AuthSession(
                    user = user,
                    token = user.token,
                    isLoggedIn = true,
                    isAdmin = user.role == "admin"
                )
            }
        }
    }

    val currentUserId: Long
        get() = _currentSession.value.user?.id ?: 1L

    // ──────────────── AUTHENTICATION ────────────────
    suspend fun login(email: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        // 1. Attempt Laravel Sanctum API login if online
        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val response = api.login(LoginRequest(email = trimmedEmail, password = trimmedPassword))
                if (response.isSuccessful && response.body()?.success == true) {
                    val authData = response.body()!!.data!!
                    val token = authData.token
                    ApiClient.setAuthToken(token)

                    // Cache user in Room
                    var localUser = database.userDao().getUserByEmail(trimmedEmail)
                    val updated = (localUser ?: UserEntity(
                        name = authData.user.name,
                        email = authData.user.email,
                        passwordHash = trimmedPassword,
                        role = authData.user.role
                    )).copy(token = token, role = authData.user.role, name = authData.user.name)

                    val savedId = database.userDao().insertUser(updated)
                    val finalUser = updated.copy(id = if (updated.id != 0L) updated.id else savedId)

                    _currentSession.value = AuthSession(
                        user = finalUser,
                        token = token,
                        isLoggedIn = true,
                        isAdmin = finalUser.role == "admin"
                    )
                    return@withContext Result.success(finalUser)
                }
            } catch (_: Exception) {
                // Network failed or server unreachable, fallback to local offline cache
            }
        }

        // 2. Offline / Local fallback authentication
        val user = database.userDao().getUserByEmail(trimmedEmail)
            ?: return@withContext Result.failure(Exception("Account not found. Verify email or connect to server."))

        val inputHash = sha256(trimmedPassword)
        if (user.passwordHash != trimmedPassword && user.passwordHash != inputHash) {
            return@withContext Result.failure(Exception("Invalid email or password."))
        }

        val token = user.token ?: "sanctum_offline_token_${user.id}"
        ApiClient.setAuthToken(token)
        val updatedUser = user.copy(token = token)
        database.userDao().updateUser(updatedUser)

        _currentSession.value = AuthSession(
            user = updatedUser,
            token = token,
            isLoggedIn = true,
            isAdmin = updatedUser.role == "admin"
        )
        Result.success(updatedUser)
    }

    suspend fun register(name: String, email: String, password: String, confirmation: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return@withContext Result.failure(Exception("All fields are required"))
        }
        if (password != confirmation) {
            return@withContext Result.failure(Exception("Password confirmation does not match"))
        }

        // 1. Attempt Laravel Sanctum API register
        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val response = api.register(RegisterRequest(name, email, password, confirmation))
                if (response.isSuccessful && response.body()?.success == true) {
                    val authData = response.body()!!.data!!
                    val token = authData.token
                    ApiClient.setAuthToken(token)

                    val newUser = UserEntity(
                        name = authData.user.name,
                        email = authData.user.email,
                        passwordHash = password,
                        role = authData.user.role,
                        token = token
                    )
                    val id = database.userDao().insertUser(newUser)
                    val createdUser = newUser.copy(id = id)

                    _currentSession.value = AuthSession(
                        user = createdUser,
                        token = token,
                        isLoggedIn = true,
                        isAdmin = false
                    )
                    return@withContext Result.success(createdUser)
                }
            } catch (_: Exception) {
                // Fallback to local
            }
        }

        // 2. Offline / Local register fallback
        val existing = database.userDao().getUserByEmail(email.trim())
        if (existing != null) {
            return@withContext Result.failure(Exception("An account already exists with this email"))
        }
        val token = "sanctum_offline_token_${System.currentTimeMillis()}"
        ApiClient.setAuthToken(token)

        val newUser = UserEntity(
            name = name.trim(),
            email = email.trim(),
            passwordHash = password.trim(),
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80",
            role = "user",
            token = token
        )
        val id = database.userDao().insertUser(newUser)
        val createdUser = newUser.copy(id = id)

        _currentSession.value = AuthSession(
            user = createdUser,
            token = token,
            isLoggedIn = true,
            isAdmin = false
        )
        Result.success(createdUser)
    }

    fun logout() {
        scope.launch(Dispatchers.IO) {
            if (NetworkUtils.isNetworkAvailable(context)) {
                try { api.logout() } catch (_: Exception) {}
            }
            val user = _currentSession.value.user
            if (user != null) {
                database.userDao().updateUser(user.copy(token = null))
            }
        }
        ApiClient.setAuthToken(null)
        _currentSession.value = AuthSession()
    }

    suspend fun switchUserRoleForAdminTesting(isAdmin: Boolean) = withContext(Dispatchers.IO) {
        val current = _currentSession.value.user ?: return@withContext
        val newRole = if (isAdmin) "admin" else "user"
        val updated = current.copy(role = newRole)
        database.userDao().updateUser(updated)
        _currentSession.value = _currentSession.value.copy(user = updated, isAdmin = isAdmin)
    }

    suspend fun updateUserProfile(name: String, avatarUrl: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val current = _currentSession.value.user ?: return@withContext Result.failure(Exception("Not logged in"))
        val updated = current.copy(name = name.trim(), avatarUrl = avatarUrl.trim())
        database.userDao().updateUser(updated)
        _currentSession.value = _currentSession.value.copy(user = updated)
        Result.success(updated)
    }

    suspend fun changePassword(oldPass: String, newPass: String): Result<Unit> = withContext(Dispatchers.IO) {
        val current = _currentSession.value.user ?: return@withContext Result.failure(Exception("Not logged in"))
        if (current.passwordHash != oldPass) {
            return@withContext Result.failure(Exception("Current password incorrect"))
        }
        val updated = current.copy(passwordHash = newPass)
        database.userDao().updateUser(updated)
        _currentSession.value = _currentSession.value.copy(user = updated)
        Result.success(Unit)
    }

    // ──────────────── HOME FEED WITH BACKEND SYNC & OFFLINE CACHE ────────────────
    fun getHomeFeed(): Flow<HomeFeedData> {
        // Trigger remote API sync in background when network is present
        if (NetworkUtils.isNetworkAvailable(context)) {
            scope.launch(Dispatchers.IO) {
                try {
                    val response = api.getHome()
                    if (response.isSuccessful && response.body()?.success == true) {
                        val data = response.body()!!.data
                        if (data != null) {
                            // Update Room cache with live Backend data
                            data.trending.forEach { database.movieDao().insertMovie(it) }
                            data.popular_movies.forEach { database.movieDao().insertMovie(it) }
                            data.popular_series.forEach { database.seriesDao().insertSeries(it) }
                            data.genres.forEach { database.genreDao().insertGenre(it) }
                            data.featured?.let { database.movieDao().insertMovie(it) }
                        }
                    }
                } catch (_: Exception) {
                    // Gracefully continue with Room cache
                }
            }
        }

        // Return reactive Room Flow cache (Single Source of Truth)
        return combine(
            database.movieDao().getAllMovies(),
            database.seriesDao().getAllSeries(),
            getContinueWatchingList(),
            database.genreDao().getAllGenres()
        ) { allMovies, allSeries, continueWatching, genres ->
            val featuredMovie = allMovies.firstOrNull { it.isFeatured } ?: allMovies.firstOrNull()
            val featuredItem = featuredMovie?.let {
                ContentItem(
                    id = it.id,
                    title = it.title,
                    poster = it.poster,
                    backdrop = it.backdrop,
                    rating = it.rating,
                    releaseYear = it.releaseDate,
                    durationMinutes = it.durationMinutes,
                    type = "movie",
                    ageRating = it.ageRating
                )
            }

            val movieItems = allMovies.map {
                ContentItem(
                    id = it.id,
                    title = it.title,
                    poster = it.poster,
                    backdrop = it.backdrop,
                    rating = it.rating,
                    releaseYear = it.releaseDate,
                    durationMinutes = it.durationMinutes,
                    type = "movie",
                    ageRating = it.ageRating
                )
            }

            val seriesItems = allSeries.map {
                ContentItem(
                    id = it.id,
                    title = it.title,
                    poster = it.poster,
                    backdrop = it.backdrop,
                    rating = it.rating,
                    releaseYear = it.releaseDate,
                    type = "series"
                )
            }

            val trendingMovies = movieItems.filter { m -> allMovies.find { it.id == m.id }?.isTrending == true }
            val popularMovies = movieItems.sortedByDescending { it.rating }
            val popularSeries = seriesItems.sortedByDescending { it.rating }

            HomeFeedData(
                featured = featuredItem,
                trending = if (trendingMovies.isNotEmpty()) trendingMovies else movieItems.take(5),
                popularMovies = popularMovies,
                popularSeries = popularSeries,
                continueWatching = continueWatching,
                latestReleases = movieItems.reversed(),
                topRated = (movieItems + seriesItems).sortedByDescending { it.rating },
                genres = genres
            )
        }
    }

    // ──────────────── CONTINUE WATCHING & DEBOUNCED PROGRESS ────────────────
    fun getContinueWatchingList(): Flow<List<ContinueWatchingItem>> {
        return database.watchHistoryDao().getUserWatchHistory(currentUserId).map { histories ->
            histories.mapNotNull { h ->
                if (h.contentType == "movie") {
                    val m = database.movieDao().getMovieById(h.contentId) ?: return@mapNotNull null
                    val pct = if (h.durationSeconds > 0) (h.progressSeconds.toFloat() / h.durationSeconds).coerceIn(0f, 1f) else 0.35f
                    ContinueWatchingItem(
                        history = h,
                        title = m.title,
                        subtitle = "${m.durationMinutes} min",
                        poster = m.poster,
                        backdrop = m.backdrop,
                        videoUrl = m.videoUrl,
                        progressSeconds = h.progressSeconds,
                        durationSeconds = h.durationSeconds,
                        progressPercentage = pct
                    )
                } else {
                    val s = database.seriesDao().getSeriesById(h.contentId) ?: return@mapNotNull null
                    val ep = h.episodeId?.let { database.episodeDao().getEpisodeById(it) }
                    val epTitle = ep?.title ?: "Episode 1"
                    val pct = if (h.durationSeconds > 0) (h.progressSeconds.toFloat() / h.durationSeconds).coerceIn(0f, 1f) else 0.45f
                    ContinueWatchingItem(
                        history = h,
                        title = s.title,
                        subtitle = epTitle,
                        poster = s.poster,
                        backdrop = s.backdrop,
                        videoUrl = ep?.videoUrl ?: s.trailerUrl,
                        progressSeconds = h.progressSeconds,
                        durationSeconds = h.durationSeconds,
                        progressPercentage = pct
                    )
                }
            }
        }
    }

    suspend fun saveProgress(
        contentId: Long,
        contentType: String,
        episodeId: Long?,
        progressSeconds: Long,
        durationSeconds: Long
    ) = withContext(Dispatchers.IO) {
        // 1. Immediately update Room cache for instantaneous responsive UI
        val history = WatchHistoryEntity(
            userId = currentUserId,
            contentId = contentId,
            contentType = contentType,
            episodeId = episodeId,
            progressSeconds = progressSeconds,
            durationSeconds = durationSeconds,
            updatedAt = System.currentTimeMillis()
        )
        database.watchHistoryDao().insertOrUpdateProgress(history)

        // 2. Synchronize to Backend API
        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                api.saveProgress(
                    ProgressRequest(
                        content_id = contentId,
                        content_type = contentType,
                        episode_id = episodeId,
                        progress_seconds = progressSeconds,
                        duration_seconds = durationSeconds
                    )
                )
            } catch (_: Exception) {}
        }
    }

    suspend fun deleteProgress(contentId: Long, contentType: String) = withContext(Dispatchers.IO) {
        database.watchHistoryDao().deleteProgress(currentUserId, contentId, contentType)
        if (NetworkUtils.isNetworkAvailable(context)) {
            try { api.deleteProgress(contentId) } catch (_: Exception) {}
        }
    }

    // ──────────────── WATCHLIST (API + ROOM CACHE) ────────────────
    fun getUserWatchlist(): Flow<List<WatchlistItem>> {
        return database.watchlistDao().getUserWatchlist(currentUserId).map { list ->
            list.mapNotNull { w ->
                if (w.contentType == "movie") {
                    val m = database.movieDao().getMovieById(w.contentId) ?: return@mapNotNull null
                    WatchlistItem(
                        watchlistId = w.id,
                        contentId = m.id,
                        contentType = "movie",
                        title = m.title,
                        poster = m.poster,
                        backdrop = m.backdrop,
                        rating = m.rating,
                        releaseYear = m.releaseDate,
                        durationMinutes = m.durationMinutes,
                        addedAt = w.addedAt
                    )
                } else {
                    val s = database.seriesDao().getSeriesById(w.contentId) ?: return@mapNotNull null
                    WatchlistItem(
                        watchlistId = w.id,
                        contentId = s.id,
                        contentType = "series",
                        title = s.title,
                        poster = s.poster,
                        backdrop = s.backdrop,
                        rating = s.rating,
                        releaseYear = s.releaseDate,
                        durationMinutes = 0,
                        addedAt = w.addedAt
                    )
                }
            }
        }
    }

    fun isInWatchlist(contentId: Long, contentType: String): Flow<Boolean> {
        return database.watchlistDao().isInWatchlist(currentUserId, contentId, contentType)
    }

    suspend fun toggleWatchlist(contentId: Long, contentType: String): Boolean = withContext(Dispatchers.IO) {
        val exists = database.watchlistDao().isInWatchlistDirect(currentUserId, contentId, contentType)
        if (exists) {
            database.watchlistDao().removeFromWatchlist(currentUserId, contentId, contentType)
            if (NetworkUtils.isNetworkAvailable(context)) {
                try { api.removeFromWatchlist(contentId) } catch (_: Exception) {}
            }
            false
        } else {
            database.watchlistDao().addToWatchlist(
                WatchlistEntity(userId = currentUserId, contentId = contentId, contentType = contentType)
            )
            if (NetworkUtils.isNetworkAvailable(context)) {
                try { api.addToWatchlist(WatchlistRequest(contentId, contentType)) } catch (_: Exception) {}
            }
            true
        }
    }

    // ──────────────── DETAILS: MOVIE ────────────────
    fun getMovieDetails(movieId: Long): Flow<MovieDetailsUi?> = flow {
        // Sync movie details from remote API if online
        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val response = api.getMovie(movieId)
                if (response.isSuccessful && response.body()?.data != null) {
                    database.movieDao().insertMovie(response.body()!!.data!!)
                }
            } catch (_: Exception) {}
        }

        val movie = database.movieDao().getMovieById(movieId)
        if (movie == null) {
            emit(null)
            return@flow
        }
        database.movieDao().incrementViews(movieId)
        val genres = database.genreDao().getGenresListForMovie(movieId)
        val cast = database.castDao().getCastListForMovie(movieId)
        val userRating = database.ratingDao().getUserRatingDirect(currentUserId, movieId, "movie")
        val isInWatchlist = database.watchlistDao().isInWatchlistDirect(currentUserId, movieId, "movie")
        val progress = database.watchHistoryDao().getWatchProgressDirect(currentUserId, movieId, "movie")

        emit(
            MovieDetailsUi(
                movie = movie,
                genres = genres,
                cast = cast,
                averageRating = movie.rating,
                ratingsCount = 284,
                userRating = userRating?.rating,
                userReview = userRating?.review,
                isInWatchlist = isInWatchlist,
                continueProgress = progress,
                similarMovies = emptyList()
            )
        )
    }

    // ──────────────── DETAILS: SERIES ────────────────
    fun getSeriesDetails(seriesId: Long): Flow<SeriesDetailsUi?> = flow {
        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val response = api.getSeriesDetails(seriesId)
                if (response.isSuccessful && response.body()?.data != null) {
                    database.seriesDao().insertSeries(response.body()!!.data!!)
                }
            } catch (_: Exception) {}
        }

        val series = database.seriesDao().getSeriesById(seriesId)
        if (series == null) {
            emit(null)
            return@flow
        }
        database.seriesDao().incrementViews(seriesId)
        val genres = database.genreDao().getGenresListForSeries(seriesId)
        val cast = database.castDao().getCastListForSeries(seriesId)
        val seasons = database.seasonDao().getSeasonsListForSeries(seriesId).map { season ->
            val eps = database.episodeDao().getEpisodesListForSeason(season.id)
            SeasonWithEpisodes(season, eps)
        }
        val userRating = database.ratingDao().getUserRatingDirect(currentUserId, seriesId, "series")
        val isInWatchlist = database.watchlistDao().isInWatchlistDirect(currentUserId, seriesId, "series")
        val progress = database.watchHistoryDao().getWatchProgressDirect(currentUserId, seriesId, "series")

        emit(
            SeriesDetailsUi(
                series = series,
                genres = genres,
                cast = cast,
                seasons = seasons,
                averageRating = series.rating,
                ratingsCount = 192,
                userRating = userRating?.rating,
                userReview = userRating?.review,
                isInWatchlist = isInWatchlist,
                continueProgress = progress,
                similarSeries = emptyList()
            )
        )
    }

    // ──────────────── RATINGS (API + ROOM CACHE) ────────────────
    suspend fun submitRating(contentId: Long, contentType: String, rating: Int, review: String) = withContext(Dispatchers.IO) {
        val entity = RatingEntity(
            userId = currentUserId,
            contentId = contentId,
            contentType = contentType,
            rating = rating,
            review = review.trim(),
            createdAt = System.currentTimeMillis()
        )
        database.ratingDao().insertOrUpdateRating(entity)

        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                api.submitRating(RatingRequest(contentId, contentType, rating, review))
            } catch (_: Exception) {}
        }
    }

    suspend fun deleteRating(contentId: Long, contentType: String) = withContext(Dispatchers.IO) {
        database.ratingDao().deleteRating(currentUserId, contentId, contentType)
        if (NetworkUtils.isNetworkAvailable(context)) {
            try { api.deleteRating(contentId) } catch (_: Exception) {}
        }
    }

    // ──────────────── SEARCH ────────────────
    fun searchContent(query: String, filter: SearchFilter): Flow<List<ContentItem>> {
        return combine(
            database.movieDao().getAllMovies(),
            database.seriesDao().getAllSeries()
        ) { movies, seriesList ->
            val movieItems = movies.map {
                ContentItem(
                    id = it.id,
                    title = it.title,
                    poster = it.poster,
                    backdrop = it.backdrop,
                    rating = it.rating,
                    releaseYear = it.releaseDate,
                    durationMinutes = it.durationMinutes,
                    type = "movie",
                    ageRating = it.ageRating
                )
            }
            val seriesItems = seriesList.map {
                ContentItem(
                    id = it.id,
                    title = it.title,
                    poster = it.poster,
                    backdrop = it.backdrop,
                    rating = it.rating,
                    releaseYear = it.releaseDate,
                    type = "series"
                )
            }

            var combined = when (filter.type.lowercase()) {
                "movie" -> movieItems
                "series" -> seriesItems
                else -> movieItems + seriesItems
            }

            if (query.isNotBlank()) {
                val q = query.trim().lowercase()
                combined = combined.filter { it.title.lowercase().contains(q) }
            }

            if (filter.minRating != null) {
                combined = combined.filter { it.rating >= filter.minRating }
            }

            if (!filter.year.isNullOrBlank()) {
                combined = combined.filter { it.releaseYear.contains(filter.year) }
            }

            when (filter.sortBy) {
                "rating" -> combined.sortedByDescending { it.rating }
                "newest" -> combined.sortedByDescending { it.releaseYear }
                else -> combined
            }
        }
    }

    // ──────────────── NOTIFICATIONS ────────────────
    fun getNotifications(): Flow<List<NotificationEntity>> {
        if (NetworkUtils.isNetworkAvailable(context)) {
            scope.launch(Dispatchers.IO) {
                try {
                    val response = api.getNotifications()
                    if (response.isSuccessful && response.body()?.data != null) {
                        response.body()!!.data!!.forEach {
                            database.notificationDao().insertNotification(it)
                        }
                    }
                } catch (_: Exception) {}
            }
        }
        return database.notificationDao().getNotificationsForUser(currentUserId)
    }

    fun getUnreadNotificationsCount(): Flow<Int> {
        return database.notificationDao().getUnreadCount(currentUserId)
    }

    suspend fun markNotificationRead(id: Long) = withContext(Dispatchers.IO) {
        database.notificationDao().markAsRead(id)
        if (NetworkUtils.isNetworkAvailable(context)) {
            try { api.markNotificationAsRead(id) } catch (_: Exception) {}
        }
    }

    suspend fun markAllNotificationsRead() = withContext(Dispatchers.IO) {
        database.notificationDao().markAllAsRead(currentUserId)
        if (NetworkUtils.isNetworkAvailable(context)) {
            try { api.markAllNotificationsAsRead() } catch (_: Exception) {}
        }
    }

    // ──────────────── ADMIN API ────────────────
    fun getAdminStats(): Flow<AdminStats> = flow {
        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val response = api.getAdminStats()
                if (response.isSuccessful && response.body()?.data != null) {
                    emit(response.body()!!.data!!)
                    return@flow
                }
            } catch (_: Exception) {}
        }

        // Offline / Room fallback
        val users = database.userDao().getUsersCount()
        val movies = database.movieDao().getMoviesCount()
        val series = database.seriesDao().getSeriesCount()
        val episodes = database.episodeDao().getEpisodesCount()
        val ratings = database.ratingDao().getRatingsCount()
        val views = database.movieDao().getTotalViews() ?: 54800L
        emit(
            AdminStats(
                usersCount = users,
                moviesCount = movies,
                seriesCount = series,
                episodesCount = episodes,
                totalViews = views,
                ratingsCount = ratings
            )
        )
    }

    fun getAllUsers(): Flow<List<UserEntity>> = database.userDao().getAllUsers()

    suspend fun toggleUserRole(userId: Long) = withContext(Dispatchers.IO) {
        val user = database.userDao().getUserById(userId) ?: return@withContext
        val newRole = if (user.role == "admin") "user" else "admin"
        database.userDao().updateUser(user.copy(role = newRole))
        if (NetworkUtils.isNetworkAvailable(context)) {
            try { api.toggleUserRole(userId) } catch (_: Exception) {}
        }
    }

    suspend fun deleteUser(userId: Long) = withContext(Dispatchers.IO) {
        database.userDao().deleteUser(userId)
        if (NetworkUtils.isNetworkAvailable(context)) {
            try { api.deleteUser(userId) } catch (_: Exception) {}
        }
    }

    suspend fun createMovie(movie: MovieEntity, genreIds: List<Long>): Long = withContext(Dispatchers.IO) {
        val id = database.movieDao().insertMovie(movie)
        genreIds.forEach { gid ->
            database.genreDao().insertMovieGenre(MovieGenreEntity(id, gid))
        }

        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                api.createMovie(movie)
            } catch (_: Exception) {}
        }
        id
    }

    suspend fun deleteMovie(movieId: Long) = withContext(Dispatchers.IO) {
        database.movieDao().deleteMovie(movieId)
        database.genreDao().deleteMovieGenres(movieId)
        if (NetworkUtils.isNetworkAvailable(context)) {
            try { api.deleteMovie(movieId) } catch (_: Exception) {}
        }
    }

    suspend fun broadcastNotification(title: String, message: String, type: String) = withContext(Dispatchers.IO) {
        database.notificationDao().insertNotification(
            NotificationEntity(
                userId = 0,
                title = title,
                message = message,
                type = type,
                isRead = false
            )
        )
        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                api.broadcastNotification(BroadcastRequest(title, message, type))
            } catch (_: Exception) {}
        }
    }

    fun updateApiBaseUrl(newUrl: String) {
        ApiClient.setBaseUrl(context, newUrl)
    }

    private fun sha256(input: String): String {
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
