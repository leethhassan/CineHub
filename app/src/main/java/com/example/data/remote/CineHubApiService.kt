package com.example.data.remote

import com.example.data.model.*
import retrofit2.Response
import retrofit2.http.*

data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)

data class AuthResponseData(
    val user: UserDto,
    val token: String
)

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val avatar_url: String? = null,
    val role: String = "user"
)

data class WatchlistRequest(
    val content_id: Long,
    val content_type: String
)

data class ProgressRequest(
    val content_id: Long,
    val content_type: String,
    val episode_id: Long? = null,
    val progress_seconds: Long,
    val duration_seconds: Long
)

data class RatingRequest(
    val content_id: Long,
    val content_type: String,
    val rating: Int,
    val review: String? = null
)

data class BroadcastRequest(
    val title: String,
    val message: String,
    val type: String = "system"
)

data class HomeResponseData(
    val featured: MovieEntity? = null,
    val trending: List<MovieEntity> = emptyList(),
    val popular_movies: List<MovieEntity> = emptyList(),
    val popular_series: List<SeriesEntity> = emptyList(),
    val genres: List<GenreEntity> = emptyList()
)

interface CineHubApiService {

    // ── Auth ──
    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponseData>>

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponseData>>

    @POST("api/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @GET("api/user/profile")
    suspend fun getProfile(): Response<ApiResponse<UserDto>>

    // ── Content ──
    @GET("api/home")
    suspend fun getHome(): Response<ApiResponse<HomeResponseData>>

    @GET("api/movies")
    suspend fun getMovies(@Query("genre_id") genreId: Long? = null): Response<ApiResponse<List<MovieEntity>>>

    @GET("api/movies/{id}")
    suspend fun getMovie(@Path("id") id: Long): Response<ApiResponse<MovieEntity>>

    @GET("api/series")
    suspend fun getSeries(): Response<ApiResponse<List<SeriesEntity>>>

    @GET("api/series/{id}")
    suspend fun getSeriesDetails(@Path("id") id: Long): Response<ApiResponse<SeriesEntity>>

    @GET("api/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("type") type: String = "all",
        @Query("sort_by") sortBy: String = "popularity"
    ): Response<ApiResponse<List<ContentItem>>>

    // ── Watchlist ──
    @GET("api/watchlist")
    suspend fun getWatchlist(): Response<ApiResponse<List<WatchlistItem>>>

    @POST("api/watchlist")
    suspend fun addToWatchlist(@Body request: WatchlistRequest): Response<ApiResponse<Any>>

    @DELETE("api/watchlist/{id}")
    suspend fun removeFromWatchlist(@Path("id") id: Long): Response<ApiResponse<Unit>>

    // ── Continue Watching & Progress ──
    @GET("api/continue-watching")
    suspend fun getContinueWatching(): Response<ApiResponse<List<ContinueWatchingItem>>>

    @POST("api/progress")
    suspend fun saveProgress(@Body request: ProgressRequest): Response<ApiResponse<Any>>

    @DELETE("api/progress/{contentId}")
    suspend fun deleteProgress(@Path("contentId") contentId: Long): Response<ApiResponse<Unit>>

    // ── Ratings ──
    @POST("api/ratings")
    suspend fun submitRating(@Body request: RatingRequest): Response<ApiResponse<Any>>

    @DELETE("api/ratings/{id}")
    suspend fun deleteRating(@Path("id") id: Long): Response<ApiResponse<Unit>>

    // ── Notifications ──
    @GET("api/notifications")
    suspend fun getNotifications(): Response<ApiResponse<List<NotificationEntity>>>

    @PUT("api/notifications/{id}/read")
    suspend fun markNotificationAsRead(@Path("id") id: Long): Response<ApiResponse<Unit>>

    @PUT("api/notifications/mark-all-read")
    suspend fun markAllNotificationsAsRead(): Response<ApiResponse<Unit>>

    // ── Admin ──
    @GET("api/admin/dashboard/stats")
    suspend fun getAdminStats(): Response<ApiResponse<AdminStats>>

    @POST("api/admin/movies")
    suspend fun createMovie(@Body movie: MovieEntity): Response<ApiResponse<MovieEntity>>

    @DELETE("api/admin/movies/{id}")
    suspend fun deleteMovie(@Path("id") id: Long): Response<ApiResponse<Unit>>

    @GET("api/admin/users")
    suspend fun getUsers(): Response<ApiResponse<List<UserEntity>>>

    @PUT("api/admin/users/{id}/toggle-role")
    suspend fun toggleUserRole(@Path("id") id: Long): Response<ApiResponse<UserEntity>>

    @DELETE("api/admin/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<ApiResponse<Unit>>

    @POST("api/admin/notifications/broadcast")
    suspend fun broadcastNotification(@Body request: BroadcastRequest): Response<ApiResponse<NotificationEntity>>
}
