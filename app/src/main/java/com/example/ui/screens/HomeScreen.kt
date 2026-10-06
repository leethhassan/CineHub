package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineHubViewModel

@Composable
fun HomeScreen(
    viewModel: CineHubViewModel,
    onNavigateToMovie: (Long) -> Unit,
    onNavigateToSeries: (Long) -> Unit,
    onNavigateToPlayer: (url: String, title: String, contentId: Long, type: String, episodeId: Long?, progress: Long) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val homeFeed by viewModel.homeFeed.collectAsStateWithLifecycle()
    val unreadNotifications by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val authSession by viewModel.authSession.collectAsStateWithLifecycle()
    val watchlist by viewModel.watchlist.collectAsStateWithLifecycle()

    var selectedGenreId by remember { mutableStateOf<Long?>(null) }

    val featured = homeFeed.featured
    val isFeaturedInWatchlist = remember(featured, watchlist) {
        if (featured == null) false else watchlist.any { it.contentId == featured.id && it.contentType == featured.type }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CineHubDarkBackground)
    ) {
        // Top App Bar
        CineHubTopBar(
            unreadCount = unreadNotifications,
            onNotificationsClick = onNavigateToNotifications,
            onAdminClick = onNavigateToAdmin,
            isAdmin = authSession.isAdmin
        )

        // Main scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
        ) {
            // Genre Filter Chips
            if (homeFeed.genres.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedGenreId == null,
                        onClick = { selectedGenreId = null },
                        label = { Text("All", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CineHubPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = CineHubSurfaceVariant,
                            labelColor = CineHubTextSecondary
                        ),
                        border = null,
                        shape = RoundedCornerShape(20.dp)
                    )
                    homeFeed.genres.forEach { genre ->
                        FilterChip(
                            selected = selectedGenreId == genre.id,
                            onClick = { selectedGenreId = genre.id },
                            label = { Text(genre.name, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CineHubPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = CineHubSurfaceVariant,
                                labelColor = CineHubTextSecondary
                            ),
                            border = null,
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            // Hero Banner
            if (featured != null) {
                HeroBanner(
                    item = featured,
                    isInWatchlist = isFeaturedInWatchlist,
                    onPlayClick = {
                        onNavigateToPlayer(
                            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                            featured.title,
                            featured.id,
                            featured.type,
                            null,
                            0
                        )
                    },
                    onDetailsClick = {
                        if (featured.type == "movie") onNavigateToMovie(featured.id) else onNavigateToSeries(featured.id)
                    },
                    onWatchlistToggle = {
                        viewModel.toggleWatchlist(featured.id, featured.type)
                    }
                )
            }

            // Continue Watching Section
            if (homeFeed.continueWatching.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = "Continue Watching")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(homeFeed.continueWatching, key = { "${it.history.contentType}_${it.history.contentId}" }) { item ->
                        ContinueWatchingCard(
                            item = item,
                            onClick = {
                                onNavigateToPlayer(
                                    item.videoUrl,
                                    item.title,
                                    item.history.contentId,
                                    item.history.contentType,
                                    item.history.episodeId,
                                    item.progressSeconds
                                )
                            }
                        )
                    }
                }
            }

            // Trending Now
            if (homeFeed.trending.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                SectionHeader(title = "Trending Now")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(homeFeed.trending, key = { "trend_${it.type}_${it.id}" }) { item ->
                        PosterCard(
                            item = item,
                            onClick = {
                                if (item.type == "movie") onNavigateToMovie(item.id) else onNavigateToSeries(item.id)
                            }
                        )
                    }
                }
            }

            // Popular Movies
            if (homeFeed.popularMovies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                SectionHeader(title = "Popular Movies")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(homeFeed.popularMovies, key = { "pop_mov_${it.id}" }) { item ->
                        PosterCard(
                            item = item,
                            onClick = { onNavigateToMovie(item.id) }
                        )
                    }
                }
            }

            // Popular Series
            if (homeFeed.popularSeries.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                SectionHeader(title = "Popular Series")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(homeFeed.popularSeries, key = { "pop_ser_${it.id}" }) { item ->
                        PosterCard(
                            item = item,
                            onClick = { onNavigateToSeries(item.id) }
                        )
                    }
                }
            }

            // Top Rated
            if (homeFeed.topRated.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                SectionHeader(title = "Top Rated Worldwide")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(homeFeed.topRated, key = { "top_${it.type}_${it.id}" }) { item ->
                        PosterCard(
                            item = item,
                            onClick = {
                                if (item.type == "movie") onNavigateToMovie(item.id) else onNavigateToSeries(item.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
