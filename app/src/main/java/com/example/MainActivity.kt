package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.VideoPlayerView
import com.example.ui.navigation.CineHubBottomNavigation
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CineHubViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CineHubViewModel by viewModels {
        val app = application as CineHubApplication
        CineHubViewModel.Factory(app.repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CineHubApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CineHubApp(viewModel: CineHubViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Search.route,
        Screen.Watchlist.route,
        Screen.ContinueWatching.route,
        Screen.Profile.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                CineHubBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else innerPadding.calculateBottomPadding() * 0)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route
            ) {
                // Splash & Onboarding
                composable(Screen.Splash.route) {
                    SplashScreen(
                        onSplashFinished = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Onboarding.route) {
                    OnboardingScreen(
                        onGetStarted = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        },
                        onLoginClick = { navController.navigate(Screen.Login.route) }
                    )
                }

                // Auth
                composable(Screen.Login.route) {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                        onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
                    )
                }

                composable(Screen.Register.route) {
                    RegisterScreen(
                        viewModel = viewModel,
                        onRegisterSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        },
                        onNavigateToLogin = { navController.popBackStack() }
                    )
                }

                composable(Screen.ForgotPassword.route) {
                    ForgotPasswordScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // Main Screens
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToMovie = { id -> navController.navigate(Screen.MovieDetails.createRoute(id)) },
                        onNavigateToSeries = { id -> navController.navigate(Screen.SeriesDetails.createRoute(id)) },
                        onNavigateToPlayer = { url, title, cid, type, epId, prog ->
                            navController.navigate(Screen.VideoPlayer.createRoute(url, title, cid, type, epId, prog))
                        },
                        onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                        onNavigateToAdmin = { navController.navigate(Screen.AdminDashboard.route) }
                    )
                }

                composable(Screen.Search.route) {
                    SearchScreen(
                        viewModel = viewModel,
                        onNavigateToMovie = { id -> navController.navigate(Screen.MovieDetails.createRoute(id)) },
                        onNavigateToSeries = { id -> navController.navigate(Screen.SeriesDetails.createRoute(id)) }
                    )
                }

                composable(Screen.Watchlist.route) {
                    WatchlistScreen(
                        viewModel = viewModel,
                        onNavigateToMovie = { id -> navController.navigate(Screen.MovieDetails.createRoute(id)) },
                        onNavigateToSeries = { id -> navController.navigate(Screen.SeriesDetails.createRoute(id)) },
                        onExploreClick = { navController.navigate(Screen.Home.route) }
                    )
                }

                composable(Screen.ContinueWatching.route) {
                    ContinueWatchingScreen(
                        viewModel = viewModel,
                        onNavigateToPlayer = { url, title, cid, type, epId, prog ->
                            navController.navigate(Screen.VideoPlayer.createRoute(url, title, cid, type, epId, prog))
                        },
                        onExploreClick = { navController.navigate(Screen.Home.route) }
                    )
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        viewModel = viewModel,
                        onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                        onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                        onNavigateToAdmin = { navController.navigate(Screen.AdminDashboard.route) },
                        onLogout = { navController.navigate(Screen.Login.route) }
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.Notifications.route) {
                    NotificationsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onContentClick = { id, type ->
                            if (type == "movie") navController.navigate(Screen.MovieDetails.createRoute(id))
                            else navController.navigate(Screen.SeriesDetails.createRoute(id))
                        }
                    )
                }

                composable(Screen.AdminDashboard.route) {
                    AdminDashboardScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // Details Screens
                composable(
                    route = Screen.MovieDetails.route,
                    arguments = listOf(navArgument("movieId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val movieId = backStackEntry.arguments?.getLong("movieId") ?: 1L
                    MovieDetailsScreen(
                        movieId = movieId,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToPlayer = { url, title, cid, type, epId, prog ->
                            navController.navigate(Screen.VideoPlayer.createRoute(url, title, cid, type, epId, prog))
                        }
                    )
                }

                composable(
                    route = Screen.SeriesDetails.route,
                    arguments = listOf(navArgument("seriesId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val seriesId = backStackEntry.arguments?.getLong("seriesId") ?: 1L
                    SeriesDetailsScreen(
                        seriesId = seriesId,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToPlayer = { url, title, cid, type, epId, prog ->
                            navController.navigate(Screen.VideoPlayer.createRoute(url, title, cid, type, epId, prog))
                        }
                    )
                }

                // Fullscreen Video Player
                composable(
                    route = Screen.VideoPlayer.route,
                    arguments = listOf(
                        navArgument("url") { type = NavType.StringType },
                        navArgument("title") { type = NavType.StringType },
                        navArgument("contentId") { type = NavType.LongType },
                        navArgument("type") { type = NavType.StringType },
                        navArgument("episodeId") {
                            type = NavType.LongType
                            defaultValue = -1L
                        },
                        navArgument("progress") {
                            type = NavType.LongType
                            defaultValue = 0L
                        }
                    )
                ) { backStackEntry ->
                    val rawUrl = backStackEntry.arguments?.getString("url") ?: ""
                    val rawTitle = backStackEntry.arguments?.getString("title") ?: "Video Player"
                    val contentId = backStackEntry.arguments?.getLong("contentId") ?: 1L
                    val type = backStackEntry.arguments?.getString("type") ?: "movie"
                    val rawEpId = backStackEntry.arguments?.getLong("episodeId") ?: -1L
                    val episodeId = if (rawEpId == -1L) null else rawEpId
                    val initialProgress = backStackEntry.arguments?.getLong("progress") ?: 0L

                    val decodedUrl = java.net.URLDecoder.decode(rawUrl, "UTF-8")
                    val decodedTitle = java.net.URLDecoder.decode(rawTitle, "UTF-8")

                    VideoPlayerView(
                        videoUrl = decodedUrl,
                        title = decodedTitle,
                        initialProgressSeconds = initialProgress,
                        onProgressUpdate = { currentSec, durationSec ->
                            viewModel.saveProgress(contentId, type, episodeId, currentSec, durationSec)
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
