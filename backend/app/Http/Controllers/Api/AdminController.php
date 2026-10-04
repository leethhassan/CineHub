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
                'total_views' => Movie::sum('views_count') + Series::sum('views_count'),
                'ratings_count' => Rating::count(),
            ]
        ]);
    }

    public function createMovie(Request $request)
    {
        $validated = $request->validate([
            'title' => 'required|string|max:255',
            'description' => 'required|string',
            'poster' => 'required|string',
            'backdrop' => 'required|string',
            'video_url' => 'required|string',
            'trailer_url' => 'nullable|string',
            'release_date' => 'required|string',
            'duration_minutes' => 'required|integer',
            'rating' => 'nullable|numeric',
            'genre_ids' => 'nullable|array',
        ]);

        $movie = Movie::create([
            'title' => $validated['title'],
            'description' => $validated['description'],
            'poster' => $validated['poster'],
            'backdrop' => $validated['backdrop'],
            'video_url' => $validated['video_url'],
            'trailer_url' => $validated['trailer_url'] ?? $validated['video_url'],
            'release_date' => $validated['release_date'],
            'duration_minutes' => $validated['duration_minutes'],
            'rating' => $validated['rating'] ?? 4.5,
            'is_published' => true,
            'is_trending' => true,
        ]);

        if (!empty($validated['genre_ids'])) {
            $movie->genres()->sync($validated['genre_ids']);
        }

        return response()->json([
            'success' => true,
            'message' => 'Movie created successfully',
            'data' => $movie
        ], 201);
    }

    public function deleteMovie($id)
    {
        Movie::findOrFail($id)->delete();
        return response()->json(['success' => true, 'message' => 'Movie deleted']);
    }

    public function users()
    {
        $users = User::orderBy('id', 'desc')->paginate(30);
        return response()->json(['success' => true, 'data' => $users]);
    }

    public function toggleRole($id)
    {
        $user = User::findOrFail($id);
        $user->role = $user->role === 'admin' ? 'user' : 'admin';
        $user->save();

        return response()->json([
            'success' => true,
            'message' => "User role updated to {$user->role}",
            'data' => $user
        ]);
    }

    public function deleteUser($id)
    {
        User::findOrFail($id)->delete();
        return response()->json(['success' => true, 'message' => 'User deleted']);
    }

    public function broadcastNotification(Request $request)
    {
        $validated = $request->validate([
            'title' => 'required|string|max:255',
            'message' => 'required|string',
            'type' => 'nullable|string',
        ]);

        $notif = Notification::create([
            'user_id' => 0, // 0 = broadcast to all
            'title' => $validated['title'],
            'message' => $validated['message'],
            'type' => $validated['type'] ?? 'system',
            'is_read' => false,
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Notification broadcasted successfully',
            'data' => $notif
        ], 201);
    }
}
