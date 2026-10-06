package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineHubViewModel

@Composable
fun WatchlistScreen(
    viewModel: CineHubViewModel,
    onNavigateToMovie: (Long) -> Unit,
    onNavigateToSeries: (Long) -> Unit,
    onExploreClick: () -> Unit
) {
    val watchlist by viewModel.watchlist.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("all") } // "all", "movie", "series"

    val filteredList = remember(watchlist, selectedFilter) {
        when (selectedFilter) {
            "movie" -> watchlist.filter { it.contentType == "movie" }
            "series" -> watchlist.filter { it.contentType == "series" }
            else -> watchlist
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CineHubDarkBackground)
            .statusBarsPadding()
    ) {
        Text(
            text = "My Watchlist",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("all" to "All (${watchlist.size})", "movie" to "Movies", "series" to "Series").forEach { (f, label) ->
                FilterChip(
                    selected = selectedFilter == f,
                    onClick = { selectedFilter = f },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CineHubPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = CineHubSurfaceVariant,
                        labelColor = CineHubTextSecondary
                    ),
                    border = null,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredList.isEmpty()) {
            EmptyStateView(
                icon = Icons.Outlined.BookmarkBorder,
                title = "Your Watchlist is Empty",
                message = "Save movies and series to your watchlist to watch them anytime.",
                actionButtonText = "Explore Titles",
                onActionClick = onExploreClick
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList, key = { "wl_${it.watchlistId}" }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (item.contentType == "movie") onNavigateToMovie(item.contentId) else onNavigateToSeries(item.contentId)
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CineHubCardBackground)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            ) {
                                AsyncImage(
                                    model = item.poster,
                                    contentDescription = item.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                IconButton(
                                    onClick = { viewModel.toggleWatchlist(item.contentId, item.contentType) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.Black.copy(alpha = 0.7f))
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }

                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = item.title,
                                    color = CineHubTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(item.releaseYear, color = CineHubTextMuted, fontSize = 11.sp)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = CineHubSecondary, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(String.format("%.1f", item.rating), color = Color.White, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContinueWatchingScreen(
    viewModel: CineHubViewModel,
    onNavigateToPlayer: (url: String, title: String, contentId: Long, type: String, episodeId: Long?, progress: Long) -> Unit,
    onExploreClick: () -> Unit
) {
    val continueWatchingList by viewModel.continueWatching.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CineHubDarkBackground)
            .statusBarsPadding()
    ) {
        Text(
            text = "Continue Watching",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        if (continueWatchingList.isEmpty()) {
            EmptyStateView(
                icon = Icons.Outlined.History,
                title = "No Watch History",
                message = "When you start watching movies and series, you can resume them here seamlessly.",
                actionButtonText = "Browse Movies",
                onActionClick = onExploreClick
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(continueWatchingList, key = { "${it.history.contentType}_${it.history.contentId}" }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onNavigateToPlayer(
                                    item.videoUrl,
                                    item.title,
                                    item.history.contentId,
                                    item.history.contentType,
                                    item.history.episodeId,
                                    item.progressSeconds
                                )
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CineHubCardBackground)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 120.dp, height = 75.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = item.backdrop.ifBlank { item.poster },
                                    contentDescription = item.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                LinearProgressIndicator(
                                    progress = { item.progressPercentage },
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .height(4.dp),
                                    color = CineHubPrimary,
                                    trackColor = Color.White.copy(alpha = 0.3f)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    color = CineHubTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.subtitle,
                                    color = CineHubAccent,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${item.progressSeconds / 60}m watched",
                                        color = CineHubTextMuted,
                                        fontSize = 11.sp
                                    )
                                    IconButton(
                                        onClick = {
                                            viewModel.deleteProgress(item.history.contentId, item.history.contentType)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove",
                                            tint = CineHubTextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
