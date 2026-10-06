<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Movie;
use App\Models\Series;
use App\Models\Season;
use App\Models\Episode;
use App\Models\User;
use App\Models\Rating;
use App\Models\Notification;
use Illuminate\Http\Request;

class AdminController extends Controller
{
    public function stats()
    {
        return response()->json([
            'success' => true,
            'data' => [
                'users_count' => User::count(),
                'movies_count' => Movie::count(),
                'series_count' => Series::count(),
                'episodes_count' => Episode::count(),
                'total_views' => (int) (Movie::sum('views_count') + Series::sum('views_count')),
                'ratings_count' => Rating::count(),
            ]
        ]);
    }

    public function createMovie(Request $request)
    {
        $validated = $request->validate([
            'title' => 'required|string|max:255',
            'description' => 'required|string',
            'poster' => 'required|string|url',
            'backdrop' => 'required|string|url',
            'video_url' => 'required|string|url',
            'trailer_url' => 'nullable|string|url',
            'release_date' => 'required|string',
            'duration_minutes' => 'required|integer|min:1',
            'rating' => 'nullable|numeric|min:0|max:5',
            'genre_ids' => 'nullable|array',
            'genre_ids.*' => 'integer|exists:genres,id',
            'age_rating' => 'nullable|string|max:10',
            'language' => 'nullable|string|max:50',
            'country' => 'nullable|string|max:50',
            'is_published' => 'nullable|boolean',
            'is_trending' => 'nullable|boolean',
            'is_featured' => 'nullable|boolean',
        ]);

        $movie = Movie::create([
            'title' => $validated['title'],
            'description' => $validated['description'],
            'poster' => $validated['poster'],
            'backdrop' => $validated['backdrop'],
            'video_url' => $validated['video_url'],
            'trailer_url' => $validated['trailer_url'] ?? $validated['video_url'],
            'release_date' => $validated['release_date'],
            'duration_minutes' => (int) $validated['duration_minutes'],
            'rating' => isset($validated['rating']) ? (float) $validated['rating'] : 0.0,
            'age_rating' => $validated['age_rating'] ?? '16+',
            'language' => $validated['language'] ?? 'English',
            'country' => $validated['country'] ?? 'USA',
            'is_published' => $validated['is_published'] ?? true,
            'is_trending' => $validated['is_trending'] ?? false,
            'is_featured' => $validated['is_featured'] ?? false,
            'views_count' => 0,
        ]);

        if (!empty($validated['genre_ids'])) {
            $movie->genres()->sync($validated['genre_ids']);
        }

        $movie->load('genres');

        return response()->json([
            'success' => true,
            'message' => 'Movie created successfully',
            'data' => $movie
        ], 201);
    }

    public function updateMovie(Request $request, $id)
    {
        $movie = Movie::findOrFail($id);

        $validated = $request->validate([
            'title' => 'sometimes|required|string|max:255',
            'description' => 'sometimes|required|string',
            'poster' => 'sometimes|required|string|url',
            'backdrop' => 'sometimes|required|string|url',
            'video_url' => 'sometimes|required|string|url',
            'trailer_url' => 'nullable|string|url',
            'release_date' => 'sometimes|required|string',
            'duration_minutes' => 'sometimes|required|integer|min:1',
            'rating' => 'nullable|numeric|min:0|max:5',
            'genre_ids' => 'nullable|array',
            'genre_ids.*' => 'integer|exists:genres,id',
            'age_rating' => 'nullable|string|max:10',
            'language' => 'nullable|string|max:50',
            'country' => 'nullable|string|max:50',
            'is_published' => 'nullable|boolean',
            'is_trending' => 'nullable|boolean',
            'is_featured' => 'nullable|boolean',
        ]);

        $movie->update($validated);

        if ($request->has('genre_ids')) {
            $movie->genres()->sync($request->genre_ids);
        }

        $movie->load('genres');

        return response()->json([
            'success' => true,
            'message' => 'Movie updated successfully',
            'data' => $movie
        ]);
    }

    public function deleteMovie($id)
    {
        $movie = Movie::findOrFail($id);
        $movie->genres()->detach();
        $movie->cast()->detach();
        Rating::where('content_id', $id)->where('content_type', 'movie')->delete();
        $movie->delete();

        return response()->json([
            'success' => true,
            'message' => 'Movie deleted successfully'
        ]);
    }

    public function createSeries(Request $request)
    {
        $validated = $request->validate([
            'title' => 'required|string|max:255',
            'description' => 'required|string',
            'poster' => 'required|string|url',
            'backdrop' => 'required|string|url',
            'trailer_url' => 'nullable|string|url',
            'release_date' => 'required|string',
            'rating' => 'nullable|numeric|min:0|max:5',
            'genre_ids' => 'nullable|array',
            'genre_ids.*' => 'integer|exists:genres,id',
            'language' => 'nullable|string|max:50',
            'country' => 'nullable|string|max:50',
            'is_published' => 'nullable|boolean',
            'is_trending' => 'nullable|boolean',
            'is_featured' => 'nullable|boolean',
        ]);

        $series = Series::create([
            'title' => $validated['title'],
            'description' => $validated['description'],
            'poster' => $validated['poster'],
            'backdrop' => $validated['backdrop'],
            'trailer_url' => $validated['trailer_url'] ?? '',
            'release_date' => $validated['release_date'],
            'rating' => isset($validated['rating']) ? (float) $validated['rating'] : 0.0,
            'language' => $validated['language'] ?? 'English',
            'country' => $validated['country'] ?? 'USA',
            'is_published' => $validated['is_published'] ?? true,
            'is_trending' => $validated['is_trending'] ?? false,
            'is_featured' => $validated['is_featured'] ?? false,
            'views_count' => 0,
        ]);

        if (!empty($validated['genre_ids'])) {
            $series->genres()->sync($validated['genre_ids']);
        }

        $series->load('genres');

        return response()->json([
            'success' => true,
            'message' => 'Series created successfully',
            'data' => $series
        ], 201);
    }

    public function deleteSeries($id)
    {
        $series = Series::findOrFail($id);
        $series->genres()->detach();
        $series->cast()->detach();
        Rating::where('content_id', $id)->where('content_type', 'series')->delete();
        $series->delete();

        return response()->json([
            'success' => true,
            'message' => 'Series deleted successfully'
        ]);
    }

    public function createSeason(Request $request)
    {
        $validated = $request->validate([
            'series_id' => 'required|integer|exists:series,id',
            'season_number' => 'required|integer|min:1',
            'title' => 'required|string|max:255',
            'release_date' => 'nullable|string',
        ]);

        $season = Season::create([
            'series_id' => $validated['series_id'],
            'season_number' => $validated['season_number'],
            'title' => $validated['title'],
            'release_date' => $validated['release_date'] ?? date('Y'),
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Season created successfully',
            'data' => $season
        ], 201);
    }

    public function createEpisode(Request $request)
    {
        $validated = $request->validate([
            'series_id' => 'required|integer|exists:series,id',
            'season_id' => 'required|integer|exists:seasons,id',
            'episode_number' => 'required|integer|min:1',
            'title' => 'required|string|max:255',
            'description' => 'required|string',
            'duration_minutes' => 'required|integer|min:1',
            'thumbnail' => 'required|string|url',
            'video_url' => 'required|string|url',
            'release_date' => 'nullable|string',
        ]);

        $episode = Episode::create([
            'series_id' => $validated['series_id'],
            'season_id' => $validated['season_id'],
            'episode_number' => $validated['episode_number'],
            'title' => $validated['title'],
            'description' => $validated['description'],
            'duration_minutes' => $validated['duration_minutes'],
            'thumbnail' => $validated['thumbnail'],
            'video_url' => $validated['video_url'],
            'release_date' => $validated['release_date'] ?? date('Y-m-d'),
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Episode created successfully',
            'data' => $episode
        ], 201);
    }

    public function users()
    {
        $users = User::select('id', 'name', 'email', 'avatar_url', 'role', 'created_at')
            ->orderBy('id', 'desc')
            ->paginate(30);

        return response()->json([
            'success' => true,
            'data' => $users
        ]);
    }

    public function toggleRole($id)
    {
        $user = User::findOrFail($id);
        $user->role = $user->role === 'admin' ? 'user' : 'admin';
        $user->save();

        return response()->json([
            'success' => true,
            'message' => "User role updated to {$user->role}",
            'data' => [
                'id' => $user->id,
                'name' => $user->name,
                'email' => $user->email,
                'role' => $user->role,
            ]
        ]);
    }

    public function deleteUser($id)
    {
        $user = User::findOrFail($id);
        $user->delete();

        return response()->json([
            'success' => true,
            'message' => 'User deleted successfully'
        ]);
    }

    public function broadcastNotification(Request $request)
    {
        $validated = $request->validate([
            'title' => 'required|string|max:255',
            'message' => 'required|string',
            'type' => 'nullable|string|in:movie,series,episode,recommendation,system',
            'action_content_id' => 'nullable|integer',
            'action_content_type' => 'nullable|string|in:movie,series',
        ]);

        $notif = Notification::create([
            'user_id' => 0, // 0 = broadcast to all
            'title' => $validated['title'],
            'message' => $validated['message'],
            'type' => $validated['type'] ?? 'system',
            'action_content_id' => $validated['action_content_id'] ?? null,
            'action_content_type' => $validated['action_content_type'] ?? null,
            'is_read' => false,
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Notification broadcasted successfully',
            'data' => $notif
        ], 201);
    }
}
