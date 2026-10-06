<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Movie;
use Illuminate\Http\Request;

class MovieController extends Controller
{
    public function index(Request $request)
    {
        $query = Movie::with(['genres', 'cast'])
            ->where('is_published', true);

        if ($request->has('genre_id')) {
            $query->whereHas('genres', function ($q) use ($request) {
                $q->where('genres.id', $request->genre_id);
            });
        }

        if ($request->boolean('trending')) {
            $query->where('is_trending', true);
        }

        if ($request->has('sort_by')) {
            switch ($request->sort_by) {
                case 'rating':
                    $query->orderBy('rating', 'desc');
                    break;
                case 'newest':
                    $query->orderBy('release_date', 'desc');
                    break;
                case 'views':
                    $query->orderBy('views_count', 'desc');
                    break;
                default:
                    $query->orderBy('id', 'desc');
            }
        } else {
            $query->orderBy('id', 'desc');
        }

        $perPage = min((int) $request->input('per_page', 20), 50);
        $movies = $query->paginate($perPage);

        return response()->json([
            'success' => true,
            'data' => $movies
        ]);
    }

    public function show($id)
    {
        $movie = Movie::with(['genres', 'cast'])
            ->where('is_published', true)
            ->findOrFail($id);

        $movie->increment('views_count');

        return response()->json([
            'success' => true,
            'data' => $movie
        ]);
    }
}
