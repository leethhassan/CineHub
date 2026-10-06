package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.CineHubRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class CineHubViewModel(
    private val repository: CineHubRepository
) : ViewModel() {

    val authSession: StateFlow<AuthSession> = repository.currentSession

    val homeFeed: StateFlow<HomeFeedData> = repository.getHomeFeed()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeFeedData()
        )

    val watchlist: StateFlow<List<WatchlistItem>> = repository.getUserWatchlist()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val continueWatching: StateFlow<List<ContinueWatchingItem>> = repository.getContinueWatchingList()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val notifications: StateFlow<List<NotificationEntity>> = repository.getNotifications()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val unreadNotificationsCount: StateFlow<Int> = repository.getUnreadNotificationsCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Search states
    val searchQuery = MutableStateFlow("")
    val searchFilter = MutableStateFlow(SearchFilter())

    val searchResults: StateFlow<List<ContentItem>> = combine(
        searchQuery.debounce(300),
        searchFilter
    ) { query, filter ->
        query to filter
    }.flatMapLatest { (query, filter) ->
        repository.searchContent(query, filter)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Selected Details
    private val _selectedMovieId = MutableStateFlow<Long?>(null)
    val selectedMovie: StateFlow<MovieDetailsUi?> = _selectedMovieId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getMovieDetails(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _selectedSeriesId = MutableStateFlow<Long?>(null)
    val selectedSeries: StateFlow<SeriesDetailsUi?> = _selectedSeriesId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getSeriesDetails(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Admin
    val adminStats: StateFlow<AdminStats> = repository.getAdminStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminStats())

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun login(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.login(email, pass)
            result.onSuccess { onSuccess() }
                .onFailure { onError(it.message ?: "Authentication failed") }
        }
    }

    fun register(name: String, email: String, pass: String, confirm: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.register(name, email, pass, confirm)
            result.onSuccess { onSuccess() }
                .onFailure { onError(it.message ?: "Registration failed") }
        }
    }

    fun logout() {
        repository.logout()
    }

    fun selectMovie(id: Long) {
        _selectedMovieId.value = id
    }

    fun selectSeries(id: Long) {
        _selectedSeriesId.value = id
    }

    fun toggleWatchlist(contentId: Long, contentType: String) {
        viewModelScope.launch {
            repository.toggleWatchlist(contentId, contentType)
        }
    }

    fun submitRating(contentId: Long, contentType: String, stars: Int, review: String) {
        viewModelScope.launch {
            repository.submitRating(contentId, contentType, stars, review)
        }
    }

    fun saveProgress(contentId: Long, contentType: String, episodeId: Long?, progressSec: Long, durationSec: Long) {
        viewModelScope.launch {
            repository.saveProgress(contentId, contentType, episodeId, progressSec, durationSec)
        }
    }

    fun deleteProgress(contentId: Long, contentType: String) {
        viewModelScope.launch {
            repository.deleteProgress(contentId, contentType)
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun updateProfile(name: String, avatarUrl: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.updateUserProfile(name, avatarUrl)
            onComplete()
        }
    }

    fun toggleAdminRole(isAdmin: Boolean) {
        viewModelScope.launch {
            repository.switchUserRoleForAdminTesting(isAdmin)
        }
    }

    fun createMovie(
        title: String,
        description: String,
        poster: String,
        backdrop: String,
        videoUrl: String,
        releaseDate: String,
        duration: Int,
        rating: Float,
        genres: List<Long>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val movie = MovieEntity(
                title = title,
                description = description,
                poster = poster.trim(),
                backdrop = backdrop.trim(),
                trailerUrl = videoUrl,
                videoUrl = videoUrl,
                releaseDate = releaseDate,
                durationMinutes = duration,
                rating = rating,
                isPublished = true,
                isTrending = true
            )
            repository.createMovie(movie, genres)
            onSuccess()
        }
    }

    fun deleteMovie(movieId: Long) {
        viewModelScope.launch {
            repository.deleteMovie(movieId)
        }
    }

    fun broadcastNotification(title: String, message: String, type: String, onSent: () -> Unit) {
        viewModelScope.launch {
            repository.broadcastNotification(title, message, type)
            onSent()
        }
    }

    fun updateApiBaseUrl(newUrl: String) {
        repository.updateApiBaseUrl(newUrl)
    }

    fun getApiBaseUrl(): String {
        return com.example.data.remote.ApiClient.getBaseUrl()
    }

    class Factory(private val repository: CineHubRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CineHubViewModel(repository) as T
        }
    }
}
