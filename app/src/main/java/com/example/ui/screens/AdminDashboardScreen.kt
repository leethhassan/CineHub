package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineHubViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: CineHubViewModel,
    onNavigateBack: () -> Unit
) {
    val stats by viewModel.adminStats.collectAsStateWithLifecycle()
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val session by viewModel.authSession.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview, 1: Add Content, 2: Users, 3: Broadcast
    var showAddMovieSuccessDialog by remember { mutableStateOf(false) }
    var showBroadcastSuccessDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CineHubDarkBackground)
            .statusBarsPadding()
    ) {
        // Admin Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Admin Studio", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("CineHub Content & System Control", color = CineHubSecondary, fontSize = 11.sp)
                }
            }

            // Role toggle switch for testing
            OutlinedButton(
                onClick = { viewModel.toggleAdminRole(!session.isAdmin) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CineHubSecondary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(if (session.isAdmin) "Admin Active" else "Switch Admin", fontSize = 11.sp)
            }
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = CineHubDarkSurface,
            contentColor = CineHubSecondary,
            edgePadding = 16.dp,
            divider = {}
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Overview") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("+ Movie") })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Users (${users.size})") })
            Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Broadcast") })
        }

        when (selectedTab) {
            0 -> AdminOverviewTab(stats = stats)
            1 -> AdminAddMovieTab(
                onMovieCreated = { title, desc, url, duration ->
                    viewModel.createMovie(
                        title = title,
                        description = desc,
                        poster = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?auto=format&fit=crop&w=600&q=80",
                        backdrop = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1200&q=80",
                        videoUrl = url.ifBlank { "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4" },
                        releaseDate = "2024",
                        duration = duration,
                        rating = 4.8f,
                        genres = listOf(1, 2)
                    ) {
                        showAddMovieSuccessDialog = true
                    }
                }
            )
            2 -> AdminUsersTab(users = users)
            3 -> AdminBroadcastTab(
                onSend = { title, message ->
                    viewModel.broadcastNotification(title, message, "system") {
                        showBroadcastSuccessDialog = true
                    }
                }
            )
        }
    }

    if (showAddMovieSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showAddMovieSuccessDialog = false },
            title = { Text("Movie Published!", color = Color.White) },
            text = { Text("The movie has been added to the database and is now live in the CineHub catalog.", color = CineHubTextSecondary) },
            confirmButton = {
                Button(
                    onClick = { showAddMovieSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CineHubPrimary)
                ) {
                    Text("OK")
                }
            },
            containerColor = CineHubDarkSurface
        )
    }

    if (showBroadcastSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastSuccessDialog = false },
            title = { Text("Notification Sent!", color = Color.White) },
            text = { Text("The notification has been broadcasted to all registered users.", color = CineHubTextSecondary) },
            confirmButton = {
                Button(
                    onClick = { showBroadcastSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CineHubPrimary)
                ) {
                    Text("OK")
                }
            },
            containerColor = CineHubDarkSurface
        )
    }
}

@Composable
fun AdminOverviewTab(stats: com.example.data.model.AdminStats) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Analytics & Metrics", color = CineHubTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Total Users", stats.usersCount.toString(), Icons.Default.People, CineHubAccent, modifier = Modifier.weight(1f))
            MetricCard("Total Movies", stats.moviesCount.toString(), Icons.Default.Movie, CineHubPrimary, modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Total Series", stats.seriesCount.toString(), Icons.Default.Tv, CineHubSecondary, modifier = Modifier.weight(1f))
            MetricCard("Episodes", stats.episodesCount.toString(), Icons.Default.VideoLibrary, Color(0xFF10B981), modifier = Modifier.weight(1f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Total Stream Views", stats.totalViews.toString(), Icons.Default.Visibility, Color(0xFF8B5CF6), modifier = Modifier.weight(1f))
            MetricCard("User Ratings", stats.ratingsCount.toString(), Icons.Default.Star, CineHubSecondary, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CineHubCardBackground)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Backend Server Status", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SQLite / Room Engine: Online & Synced", color = Color(0xFF10B981), fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("REST Endpoints: /api/v1 compatible", color = CineHubTextMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CineHubCardBackground)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = CineHubTextSecondary, fontSize = 12.sp)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminAddMovieTab(
    onMovieCreated: (title: String, desc: String, url: String, duration: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4") }
    var durationText by remember { mutableStateOf("115") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Add New Movie to Catalog", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Movie Title") },
            modifier = Modifier.fillMaxWidth().testTag("admin_movie_title_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Synopsis / Storyline") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        OutlinedTextField(
            value = videoUrl,
            onValueChange = { videoUrl = it },
            label = { Text("Video Stream URL (MP4 / HLS)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        OutlinedTextField(
            value = durationText,
            onValueChange = { durationText = it },
            label = { Text("Duration (Minutes)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    val duration = durationText.toIntOrNull() ?: 100
                    onMovieCreated(title, description, videoUrl, duration)
                    title = ""
                    description = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("admin_save_movie_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CineHubPrimary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Publish Movie", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminUsersTab(users: List<com.example.data.model.UserEntity>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(users, key = { it.id }) { u ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = CineHubCardBackground)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CineHubSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = CineHubTextSecondary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(u.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(u.email, color = CineHubTextMuted, fontSize = 12.sp)
                    }
                    Surface(
                        color = if (u.role == "admin") CineHubSecondary.copy(alpha = 0.2f) else CineHubSurfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = u.role.uppercase(),
                            color = if (u.role == "admin") CineHubSecondary else CineHubTextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminBroadcastTab(
    onSend: (title: String, message: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Send Notification Alert to All Users", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Notification Title") },
            modifier = Modifier.fillMaxWidth().testTag("broadcast_title_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Notification Message") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Button(
            onClick = {
                if (title.isNotBlank() && message.isNotBlank()) {
                    onSend(title, message)
                    title = ""
                    message = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("broadcast_send_button"),
            colors = ButtonDefaults.buttonColors(containerColor = CineHubPrimary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Broadcast Notification", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}
