package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.SearchFilter
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PosterCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineHubViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    viewModel: CineHubViewModel,
    onNavigateToMovie: (Long) -> Unit,
    onNavigateToSeries: (Long) -> Unit
) {
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filter by viewModel.searchFilter.collectAsStateWithLifecycle()
    val results by viewModel.searchResults.collectAsStateWithLifecycle()

    val popularSuggestions = listOf("Big Buck Bunny", "Sintel", "Tears of Steel", "Elephants Dream", "Animation", "Adventure", "Sci-Fi", "Open Movies")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CineHubDarkBackground)
            .statusBarsPadding()
    ) {
        // Search Input Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.searchQuery.value = it },
                placeholder = { Text("Search movies, series, genres...", color = CineHubTextMuted) },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = "Search", tint = CineHubTextSecondary)
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = CineHubTextSecondary)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input_field"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CineHubPrimary,
                    unfocusedBorderColor = CineHubBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CineHubDarkSurface,
                    unfocusedContainerColor = CineHubDarkSurface
                ),
                singleLine = true
            )
        }

        // Type filter pills (All / Movies / Series)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("all" to "All", "movie" to "Movies", "series" to "Series").forEach { (typeVal, label) ->
                val isSelected = filter.type == typeVal
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.searchFilter.value = filter.copy(type = typeVal) },
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

            Spacer(modifier = Modifier.weight(1f))

            // Sort Dropdown
            var sortMenuExpanded by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(
                    onClick = { sortMenuExpanded = true },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Sort, contentDescription = null, tint = CineHubSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when (filter.sortBy) {
                            "rating" -> "Top Rated"
                            "newest" -> "Newest"
                            else -> "Popular"
                        },
                        fontSize = 12.sp,
                        color = CineHubTextPrimary
                    )
                }

                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false },
                    modifier = Modifier.background(CineHubDarkSurface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Popularity", color = Color.White) },
                        onClick = {
                            viewModel.searchFilter.value = filter.copy(sortBy = "popularity")
                            sortMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Top Rated", color = Color.White) },
                        onClick = {
                            viewModel.searchFilter.value = filter.copy(sortBy = "rating")
                            sortMenuExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Newest Releases", color = Color.White) },
                        onClick = {
                            viewModel.searchFilter.value = filter.copy(sortBy = "newest")
                            sortMenuExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Suggestions if empty query
        if (query.isBlank() && results.isEmpty()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Trending Searches",
                    color = CineHubTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    popularSuggestions.forEach { term ->
                        Surface(
                            color = CineHubSurfaceVariant,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.clickable {
                                viewModel.searchQuery.value = term
                            }
                        ) {
                            Text(
                                text = term,
                                color = CineHubTextSecondary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        } else if (results.isEmpty()) {
            EmptyStateView(
                icon = Icons.Outlined.Search,
                title = "No Results Found",
                message = "We couldn't find any titles matching \"$query\". Try checking for typos or explore other genres."
            )
        } else {
            // Results Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(results, key = { "search_${it.type}_${it.id}" }) { item ->
                    PosterCard(
                        item = item,
                        onClick = {
                            if (item.type == "movie") onNavigateToMovie(item.id) else onNavigateToSeries(item.id)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
