package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import com.example.ui.viewmodel.CineHubViewModel

@Composable
fun NotificationsScreen(
    viewModel: CineHubViewModel,
    onNavigateBack: () -> Unit,
    onContentClick: (id: Long, type: String) -> Unit
) {
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CineHubDarkBackground)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Notifications", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            if (notifications.any { !it.isRead }) {
                TextButton(onClick = { viewModel.markAllNotificationsRead() }) {
                    Text("Mark all as read", color = CineHubAccent, fontSize = 13.sp)
                }
            }
        }

        if (notifications.isEmpty()) {
            EmptyStateView(
                icon = Icons.Outlined.Notifications,
                title = "No Notifications",
                message = "You're all caught up! You'll receive updates about new movies, episodes, and recommendations here."
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(notifications, key = { it.id }) { notif ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.markNotificationRead(notif.id)
                                if (notif.actionContentId != null && notif.actionContentType != null) {
                                    onContentClick(notif.actionContentId, notif.actionContentType)
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (notif.isRead) CineHubDarkSurface else CineHubCardBackground
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (notif.type) {
                                            "movie" -> CineHubPrimary.copy(alpha = 0.2f)
                                            "series" -> CineHubAccent.copy(alpha = 0.2f)
                                            else -> CineHubSecondary.copy(alpha = 0.2f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (notif.type) {
                                        "movie" -> Icons.Default.Movie
                                        "series" -> Icons.Default.Tv
                                        else -> Icons.Default.Notifications
                                    },
                                    contentDescription = null,
                                    tint = when (notif.type) {
                                        "movie" -> CineHubPrimary
                                        "series" -> CineHubAccent
                                        else -> CineHubSecondary
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.title,
                                        color = if (notif.isRead) CineHubTextPrimary else Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = if (notif.isRead) FontWeight.SemiBold else FontWeight.Bold
                                    )
                                    if (!notif.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(CineHubPrimary)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = notif.message,
                                    color = CineHubTextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
