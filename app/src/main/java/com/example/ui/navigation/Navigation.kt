package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")

    // Main App Navigation
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Watchlist : Screen("watchlist")
    data object ContinueWatching : Screen("continue_watching")
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")
    data object Notifications : Screen("notifications")
    data object AdminDashboard : Screen("admin_dashboard")

    // Details & Player
    data object MovieDetails : Screen("movie_details/{movieId}") {
        fun createRoute(movieId: Long) = "movie_details/$movieId"
    }
    data object SeriesDetails : Screen("series_details/{seriesId}") {
        fun createRoute(seriesId: Long) = "series_details/$seriesId"
    }
    data object VideoPlayer : Screen("video_player?url={url}&title={title}&contentId={contentId}&type={type}&episodeId={episodeId}&progress={progress}") {
        fun createRoute(
            url: String,
            title: String,
            contentId: Long,
            type: String,
            episodeId: Long? = null,
            progress: Long = 0
        ): String {
            val encodedUrl = java.net.URLEncoder.encode(url, "UTF-8")
            val encodedTitle = java.net.URLEncoder.encode(title, "UTF-8")
            val epPart = if (episodeId != null) "&episodeId=$episodeId" else ""
            return "video_player?url=$encodedUrl&title=$encodedTitle&contentId=$contentId&type=$type$epPart&progress=$progress"
        }
    }
}

data class BottomNavItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Screen.Home.route, Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem("Search", Screen.Search.route, Icons.Filled.Search, Icons.Outlined.Search),
    BottomNavItem("Watchlist", Screen.Watchlist.route, Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder),
    BottomNavItem("Continue", Screen.ContinueWatching.route, Icons.Filled.PlayCircle, Icons.Outlined.PlayCircleOutline),
    BottomNavItem("Profile", Screen.Profile.route, Icons.Filled.Person, Icons.Outlined.Person)
)

@Composable
fun CineHubBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = CineHubDarkSurface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("cinehub_bottom_nav")
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        onNavigate(item.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CineHubPrimary,
                    selectedTextColor = CineHubPrimary,
                    indicatorColor = CineHubPrimary.copy(alpha = 0.15f),
                    unselectedIconColor = CineHubTextSecondary,
                    unselectedTextColor = CineHubTextSecondary
                )
            )
        }
    }
}
