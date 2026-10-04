<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\HomeController;
use App\Http\Controllers\Api\MovieController;
use App\Http\Controllers\Api\SeriesController;
use App\Http\Controllers\Api\SearchController;
use App\Http\Controllers\Api\WatchlistController;
use App\Http\Controllers\Api\WatchHistoryController;
use App\Http\Controllers\Api\RatingController;
use App\Http\Controllers\Api\NotificationController;
use App\Http\Controllers\Api\AdminController;

/*
|--------------------------------------------------------------------------
| CineHub REST API Routes
|--------------------------------------------------------------------------
*/

// Public Authentication Routes
Route::post('/register', [AuthController::class, 'register']);
Route::post('/login', [AuthController::class, 'login']);

// Public Catalog Routes
Route::get('/home', [HomeController::class, 'index']);
Route::get('/movies', [MovieController::class, 'index']);
Route::get('/movies/{id}', [MovieController::class, 'show']);
Route::get('/series', [SeriesController::class, 'index']);
Route::get('/series/{id}', [SeriesController::class, 'show']);
Route::get('/series/{id}/seasons', [SeriesController::class, 'seasons']);
Route::get('/seasons/{id}/episodes', [SeriesController::class, 'episodes']);
Route::get('/search', [SearchController::class, 'search']);

// Protected User Routes (Laravel Sanctum)
Route::middleware('auth:sanctum')->group(function () {
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::get('/user/profile', [AuthController::class, 'profile']);
    Route::put('/user/profile', [AuthController::class, 'updateProfile']);
    Route::put('/user/password', [AuthController::class, 'updatePassword']);

    // Watchlist
    Route::get('/watchlist', [WatchlistController::class, 'index']);
    Route::post('/watchlist', [WatchlistController::class, 'store']);
    Route::delete('/watchlist/{id}', [WatchlistController::class, 'destroy']);

    // Continue Watching / Progress
    Route::get('/continue-watching', [WatchHistoryController::class, 'index']);
    Route::post('/progress', [WatchHistoryController::class, 'saveProgress']);
    Route::delete('/progress/{contentId}', [WatchHistoryController::class, 'removeProgress']);

    // Ratings & Reviews
    Route::post('/ratings', [RatingController::class, 'store']);
    Route::put('/ratings/{id}', [RatingController::class, 'update']);
    Route::delete('/ratings/{id}', [RatingController::class, 'destroy']);

    // Notifications
    Route::get('/notifications', [NotificationController::class, 'index']);
    Route::put('/notifications/{id}/read', [NotificationController::class, 'markAsRead']);
    Route::put('/notifications/mark-all-read', [NotificationController::class, 'markAllAsRead']);

    // Admin Dashboard Routes (Requires role = 'admin')
    Route::middleware('admin')->prefix('admin')->group(function () {
        Route::get('/dashboard/stats', [AdminController::class, 'stats']);
        
        // Movie Management
        Route::post('/movies', [AdminController::class, 'createMovie']);
        Route::put('/movies/{id}', [AdminController::class, 'updateMovie']);
        Route::delete('/movies/{id}', [AdminController::class, 'deleteMovie']);

        // Series & Season Management
        Route::post('/series', [AdminController::class, 'createSeries']);
        Route::delete('/series/{id}', [AdminController::class, 'deleteSeries']);
        Route::post('/seasons', [AdminController::class, 'createSeason']);
        Route::post('/episodes', [AdminController::class, 'createEpisode']);

        // User Management
        Route::get('/users', [AdminController::class, 'users']);
        Route::put('/users/{id}/toggle-role', [AdminController::class, 'toggleRole']);
        Route::delete('/users/{id}', [AdminController::class, 'deleteUser']);

        // Notification Broadcasting
        Route::post('/notifications/broadcast', [AdminController::class, 'broadcastNotification']);
    });
});
