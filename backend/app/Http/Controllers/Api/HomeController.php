<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Movie;
use App\Models\Series;
use App\Models\Genre;
use App\Models\WatchHistory;
use Illuminate\Http\Request;

class HomeController extends Controller
{
    public function index(Request $request)
    {
        $featured = Movie::with(['genres'])->where('is_featured', true)->where('is_published', true)->first()
            ?? Movie::with(['genres'])->where('is_published', true)->first();

        $trendingMovies = Movie::with(['genres'])->where('is_trending', true)->where('is_published', true)->take(10)->get();
        $popularMovies = Movie::with(['genres'])->where('is_published', true)->orderBy('rating', 'desc')->take(10)->get();
        $popularSeries = Series::with(['genres'])->where('is_published', true)->orderBy('rating', 'desc')->take(10)->get();
        $genres = Genre::all();

        $continueWatching = [];
        if ($request->user()) {
            $continueWatching = WatchHistory::where('user_id', $request->user()->id)
                ->orderBy('updated_at', 'desc')
                ->take(10)
                ->get();
        }

        return response()->json([
            'success' => true,
            'data' => [
                'featured' => $featured,
                'trending' => $trendingMovies,
                'popular_movies' => $popularMovies,
                'popular_series' => $popularSeries,
                'continue_watching' => $continueWatching,
                'genres' => $genres,
            ]
        ]);
    }
}
