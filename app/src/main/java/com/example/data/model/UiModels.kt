package com.example.data.model

data class ContentItem(
    val id: Long,
    val title: String,
    val poster: String,
    val backdrop: String,
    val rating: Float,
    val releaseYear: String,
    val durationMinutes: Int = 0,
    val type: String, // "movie" or "series"
    val genres: List<String> = emptyList(),
    val ageRating: String = "16+"
)

data class MovieDetailsUi(
    val movie: MovieEntity,
    val genres: List<GenreEntity>,
    val cast: List<CastEntity>,
    val averageRating: Float,
    val ratingsCount: Int,
    val userRating: Int? = null,
    val userReview: String? = null,
    val isInWatchlist: Boolean = false,
    val continueProgress: WatchHistoryEntity? = null,
    val similarMovies: List<ContentItem> = emptyList()
)

data class SeriesDetailsUi(
    val series: SeriesEntity,
    val genres: List<GenreEntity>,
    val cast: List<CastEntity>,
    val seasons: List<SeasonWithEpisodes>,
    val averageRating: Float,
    val ratingsCount: Int,
    val userRating: Int? = null,
    val userReview: String? = null,
    val isInWatchlist: Boolean = false,
    val continueProgress: WatchHistoryEntity? = null,
    val similarSeries: List<ContentItem> = emptyList()
)

data class SeasonWithEpisodes(
    val season: SeasonEntity,
    val episodes: List<EpisodeEntity>
)

data class ContinueWatchingItem(
    val history: WatchHistoryEntity,
    val title: String,
    val subtitle: String,
    val poster: String,
    val backdrop: String,
    val videoUrl: String,
    val progressSeconds: Long,
    val durationSeconds: Long,
    val progressPercentage: Float
)

data class WatchlistItem(
    val watchlistId: Long,
    val contentId: Long,
    val contentType: String,
    val title: String,
    val poster: String,
    val backdrop: String,
    val rating: Float,
    val releaseYear: String,
    val durationMinutes: Int,
    val addedAt: Long
)

data class HomeFeedData(
    val featured: ContentItem? = null,
    val trending: List<ContentItem> = emptyList(),
    val popularMovies: List<ContentItem> = emptyList(),
    val popularSeries: List<ContentItem> = emptyList(),
    val continueWatching: List<ContinueWatchingItem> = emptyList(),
    val latestReleases: List<ContentItem> = emptyList(),
    val topRated: List<ContentItem> = emptyList(),
    val genres: List<GenreEntity> = emptyList()
)

data class SearchFilter(
    val query: String = "",
    val type: String = "all", // "all", "movie", "series"
    val genreId: Long? = null,
    val year: String? = null,
    val minRating: Float? = null,
    val sortBy: String = "popularity" // "popularity", "newest", "rating"
)

data class AdminStats(
    val usersCount: Int = 0,
    val moviesCount: Int = 0,
    val seriesCount: Int = 0,
    val episodesCount: Int = 0,
    val totalViews: Long = 0,
    val ratingsCount: Int = 0
)

data class AuthSession(
    val user: UserEntity? = null,
    val token: String? = null,
    val isLoggedIn: Boolean = false,
    val isAdmin: Boolean = false
)
