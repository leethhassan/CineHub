package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineHubViewModel

@Composable
fun ProfileScreen(
    viewModel: CineHubViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    val session by viewModel.authSession.collectAsStateWithLifecycle()
    val watchlist by viewModel.watchlist.collectAsStateWithLifecycle()
    val continueWatching by viewModel.continueWatching.collectAsStateWithLifecycle()

    var showEditProfileDialog by remember { mutableStateOf(false) }

    val user = session.user
    val name = user?.name ?: "Guest User"
    val email = user?.email ?: "guest@cinehub.com"
    val avatar = user?.avatarUrl?.ifBlank {
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80"
    } ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=400&q=80"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CineHubDarkBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        Text(
            text = "My Profile",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // Profile Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CineHubDarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                ) {
                    AsyncImage(
                        model = avatar,
                        contentDescription = name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = email,
                    color = CineHubTextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = if (session.isAdmin) CineHubSecondary.copy(alpha = 0.2f) else CineHubPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = if (session.isAdmin) "ADMINISTRATOR" else "PREMIUM VIP",
                        color = if (session.isAdmin) CineHubSecondary else CineHubPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { showEditProfileDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("edit_profile_button")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Profile", fontSize = 13.sp, color = CineHubTextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ProfileStatCard(title = "Watchlist", value = watchlist.size.toString(), modifier = Modifier.weight(1f))
            ProfileStatCard(title = "In Progress", value = continueWatching.size.toString(), modifier = Modifier.weight(1f))
            ProfileStatCard(title = "Account", value = if (session.isAdmin) "Admin" else "User", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Menu Options
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CineHubCardBackground)
        ) {
            Column {
                ProfileMenuItem(
                    icon = Icons.Outlined.AdminPanelSettings,
                    title = "Admin Dashboard",
                    subtitle = if (session.isAdmin) "Manage catalog, users & alerts" else "Tap to switch to Admin mode",
                    onClick = {
                        if (!session.isAdmin) {
                            viewModel.toggleAdminRole(true)
                        }
                        onNavigateToAdmin()
                    },
                    iconTint = CineHubSecondary
                )
                HorizontalDivider(color = CineHubBorder)

                ProfileMenuItem(
                    icon = Icons.Outlined.Notifications,
                    title = "Notifications",
                    subtitle = "System alerts, new releases",
                    onClick = onNavigateToNotifications
                )
                HorizontalDivider(color = CineHubBorder)

                ProfileMenuItem(
                    icon = Icons.Outlined.Settings,
                    title = "App Settings",
                    subtitle = "Streaming quality, Backend API URL",
                    onClick = onNavigateToSettings
                )
                HorizontalDivider(color = CineHubBorder)

                ProfileMenuItem(
                    icon = Icons.Outlined.Security,
                    title = "Account Security",
                    subtitle = "Change password & Sanctum token",
                    onClick = { /* handled in settings */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Logout Button
        Button(
            onClick = {
                viewModel.logout()
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = CineHubSurfaceVariant),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp)
                .testTag("logout_button")
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, tint = CineHubError)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out", color = CineHubError, fontWeight = FontWeight.Bold)
        }
    }

    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(name) }
        var editAvatar by remember { mutableStateOf(avatar) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = editAvatar,
                        onValueChange = { editAvatar = it },
                        label = { Text("Avatar URL") },
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
                        viewModel.updateProfile(editName, editAvatar) {
                            showEditProfileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CineHubPrimary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = CineHubTextSecondary)
                }
            },
            containerColor = CineHubDarkSurface
        )
    }
}

@Composable
fun ProfileStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CineHubCardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(title, color = CineHubTextMuted, fontSize = 11.sp)
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconTint: Color = CineHubAccent
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CineHubSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = CineHubTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = CineHubTextMuted, fontSize = 12.sp)
        }

        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CineHubTextMuted)
    }
}

@Composable
fun SettingsScreen(
    viewModel: CineHubViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedQuality by remember { mutableStateOf("1080p Full HD") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var cellularStreaming by remember { mutableStateOf(false) }
    var apiUrl by remember { mutableStateOf(viewModel.getApiBaseUrl()) }
    var saveSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CineHubDarkBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 60.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Settings", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Playback Settings
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CineHubCardBackground)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Video Playback", color = CineHubAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Default Stream Quality", color = Color.White, fontSize = 14.sp)
                        Text(selectedQuality, color = CineHubTextMuted, fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            selectedQuality = if (selectedQuality == "Auto") "Standard" else "Auto"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CineHubSurfaceVariant)
                    ) {
                        Text("Switch", fontSize = 12.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Stream over Cellular Data", color = Color.White, fontSize = 14.sp)
                        Text("Save mobile bandwidth", color = CineHubTextMuted, fontSize = 12.sp)
                    }
                    Switch(
                        checked = cellularStreaming,
                        onCheckedChange = { cellularStreaming = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = CineHubPrimary)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Backend API Configuration
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CineHubCardBackground)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Backend & API Architecture", color = CineHubSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "CineHub connects to your live Laravel REST API. If the server is offline or unreachable, it smoothly serves and queues via local Room cache.",
                    color = CineHubTextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = apiUrl,
                    onValueChange = {
                        apiUrl = it
                        saveSuccess = false
                    },
                    label = { Text("Remote Laravel REST URL") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.updateApiBaseUrl(apiUrl)
                        saveSuccess = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CineHubPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save & Reconnect", fontWeight = FontWeight.Bold)
                }

                if (saveSuccess) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Base API URL saved. Retrofit client reconfigured.",
                        color = CineHubSuccess,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
