package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.EpisodeEntity
import com.example.ui.components.SectionHeader
import com.example.ui.components.StarRatingBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineHubViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MovieDetailsScreen(
    movieId: Long,
    viewModel: CineHubViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (url: String, title: String, contentId: Long, type: String, episodeId: Long?, progress: Long) -> Unit
) {
    LaunchedEffect(movieId) {
        viewModel.selectMovie(movieId)
    }

    val details by viewModel.selectedMovie.collectAsStateWithLifecycle()
    var showRatingDialog by remember { mutableStateOf(false) }

    if (details == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CineHubDarkBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = CineHubPrimary)
        }
        return
    }

    val movie = details!!.movie
    val isInWatchlist = details!!.isInWatchlist
    val continueProgress = details!!.continueProgress

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CineHubDarkBackground)
            .padding(bottom = 60.dp)
    ) {
        // Backdrop Header with Back & Watchlist buttons
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncImage(
                    model = movie.backdrop.ifBlank { movie.poster },
                    contentDescription = movie.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Transparent,
                                    CineHubDarkBackground
                                )
                            )
                        )
                )

                // Top Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    IconButton(
                        onClick = { viewModel.toggleWatchlist(movie.id, "movie") },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .testTag("movie_details_watchlist_button")
                    ) {
                        Icon(
                            imageVector = if (isInWatchlist) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Watchlist",
                            tint = if (isInWatchlist) CineHubPrimary else Color.White
                        )
                    }
                }
            }
        }

        // Title and Metadata Info
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = movie.title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = CineHubSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", movie.rating),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text("•", color = CineHubTextMuted)
                    Text(movie.releaseDate, color = CineHubTextSecondary, fontSize = 13.sp)
                    Text("•", color = CineHubTextMuted)
                    Text("${movie.durationMinutes} min", color = CineHubTextSecondary, fontSize = 13.sp)
                    Text("•", color = CineHubTextMuted)
                    Surface(
                        color = CineHubSurfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = movie.ageRating,
                            color = CineHubAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Genres Chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    details!!.genres.forEach { genre ->
                        Surface(
                            color = CineHubCardBackground,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = genre.name,
                                color = CineHubTextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Watch Button
                val hasProgress = continueProgress != null && continueProgress.progressSeconds > 10
                Button(
                    onClick = {
                        val startSec = continueProgress?.progressSeconds ?: 0L
                        onNavigateToPlayer(movie.videoUrl, movie.title, movie.id, "movie", null, startSec)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("movie_watch_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CineHubPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasProgress) "Resume from ${continueProgress!!.progressSeconds / 60}m" else "Watch Movie",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Synopsis
                Text(
                    text = "Storyline",
                    color = CineHubTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = movie.description,
                    color = CineHubTextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            }
        }

        // Cast Members
        if (details!!.cast.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                SectionHeader(title = "Starring Cast")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(details!!.cast, key = { it.id }) { actor ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(80.dp)
                        ) {
                            AsyncImage(
                                model = actor.avatarUrl,
                                contentDescription = actor.name,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = actor.name,
                                color = CineHubTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = actor.characterName,
                                color = CineHubTextMuted,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Rating and Review Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CineHubCardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Rate this Movie",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StarRatingBar(
                            currentRating = details!!.userRating ?: 0,
                            onRatingSelected = { stars ->
                                viewModel.submitRating(movie.id, "movie", stars, details!!.userReview ?: "")
                            }
                        )
                        OutlinedButton(
                            onClick = { showRatingDialog = true },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (details!!.userReview.isNullOrBlank()) "Write Review" else "Edit Review", fontSize = 12.sp)
                        }
                    }

                    if (!details!!.userReview.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "\"${details!!.userReview}\"",
                            color = CineHubTextSecondary,
                            fontSize = 13.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }
    }

    // Rating / Review Dialog
    if (showRatingDialog) {
        var reviewText by remember { mutableStateOf(details!!.userReview ?: "") }
        var selectedStars by remember { mutableIntStateOf(details!!.userRating ?: 5) }

        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = { Text("Your Review", color = Color.White) },
            text = {
                Column {
                    StarRatingBar(
                        currentRating = selectedStars,
                        onRatingSelected = { selectedStars = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        placeholder = { Text("What did you think of the film?", color = CineHubTextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitRating(movie.id, "movie", selectedStars, reviewText)
                        showRatingDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CineHubPrimary)
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRatingDialog = false }) {
                    Text("Cancel", color = CineHubTextSecondary)
                }
            },
            containerColor = CineHubDarkSurface
        )
    }
}

@Composable
fun SeriesDetailsScreen(
    seriesId: Long,
    viewModel: CineHubViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (url: String, title: String, contentId: Long, type: String, episodeId: Long?, progress: Long) -> Unit
) {
    LaunchedEffect(seriesId) {
        viewModel.selectSeries(seriesId)
    }

    val details by viewModel.selectedSeries.collectAsStateWithLifecycle()
    var selectedSeasonIndex by remember { mutableIntStateOf(0) }

    if (details == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CineHubDarkBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = CineHubPrimary)
        }
        return
    }

    val series = details!!.series
    val seasons = details!!.seasons
    val isInWatchlist = details!!.isInWatchlist
    val currentSeason = seasons.getOrNull(selectedSeasonIndex)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CineHubDarkBackground)
            .padding(bottom = 60.dp)
    ) {
        // Backdrop Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                AsyncImage(
                    model = series.backdrop.ifBlank { series.poster },
                    contentDescription = series.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent, CineHubDarkBackground)
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    IconButton(
                        onClick = { viewModel.toggleWatchlist(series.id, "series") },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = if (isInWatchlist) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Watchlist",
                            tint = if (isInWatchlist) CineHubPrimary else Color.White
                        )
                    }
                }
            }
        }

        // Series Info
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = series.title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = CineHubSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(String.format("%.1f", series.rating), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("•", color = CineHubTextMuted)
                    Text("${seasons.size} Seasons", color = CineHubTextSecondary, fontSize = 13.sp)
                    Text("•", color = CineHubTextMuted)
                    Text(series.releaseDate, color = CineHubTextSecondary, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = series.description,
                    color = CineHubTextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Seasons Tab Selector
        if (seasons.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    seasons.forEachIndexed { index, s ->
                        FilterChip(
                            selected = selectedSeasonIndex == index,
                            onClick = { selectedSeasonIndex = index },
                            label = { Text("Season ${s.season.seasonNumber}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CineHubPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = CineHubSurfaceVariant,
                                labelColor = CineHubTextSecondary
                            ),
                            border = null,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Episodes List
        if (currentSeason != null) {
            items(currentSeason.episodes, key = { it.id }) { ep ->
                EpisodeItemRow(
                    episode = ep,
                    onPlayClick = {
                        onNavigateToPlayer(ep.videoUrl, "${series.title} - ${ep.title}", series.id, "series", ep.id, 0)
                    }
                )
            }
        }
    }
}

@Composable
fun EpisodeItemRow(
    episode: EpisodeEntity,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onPlayClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CineHubCardBackground)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 110.dp, height = 70.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = episode.thumbnail,
                    contentDescription = episode.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${episode.episodeNumber}. ${episode.title}",
                    color = CineHubTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = episode.description,
                    color = CineHubTextMuted,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${episode.durationMinutes} min",
                    color = CineHubAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
