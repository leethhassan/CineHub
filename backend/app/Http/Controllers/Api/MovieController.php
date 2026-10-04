<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Movie;
use Illuminate\Http\Request;

class MovieController extends Controller
{
    public function index(Request $request)
    {
        $query = Movie::with(['genres', 'cast'])->where('is_published', true);

        if ($request->has('genre_id')) {
            $query->whereHas('genres', function ($q) use ($request) {
                $q->where('genres.id', $request->genre_id);
            });
        }

        if ($request->has('trending')) {
            $query->where('is_trending', true);
        }

        $movies = $query->orderBy('id', 'desc')->paginate(20);

        return response()->json([
            'success' => true,
            'data' => $movies
        ]);
    }

    public function show($id)
    {
        $movie = Movie::with(['genres', 'cast', 'ratings'])->findOrFail($id);
        $movie->increment('views_count');

        return response()->json([
            'success' => true,
            'data' => $movie
        ]);
    }
}
