<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Movie;
use App\Models\Series;
use Illuminate\Http\Request;

class SearchController extends Controller
{
    public function search(Request $request)
    {
        $query = $request->input('q', '');
        $type = $request->input('type', 'all'); // 'all', 'movie', 'series'
        $genreId = $request->input('genre_id');
        $minRating = $request->input('min_rating');
        $sortBy = $request->input('sort_by', 'popularity'); // 'popularity', 'rating', 'newest'

        $movies = collect();
        $series = collect();

        if ($type === 'all' || $type === 'movie') {
            $mQuery = Movie::with('genres')->where('is_published', true);
            if ($query) {
                $mQuery->where('title', 'like', "%{$query}%");
            }
            if ($genreId) {
                $mQuery->whereHas('genres', fn($q) => $q->where('genres.id', $genreId));
            }
            if ($minRating) {
                $mQuery->where('rating', '>=', $minRating);
            }
            $movies = $mQuery->get()->map(function ($item) {
                $item->content_type = 'movie';
                return $item;
            });
        }

        if ($type === 'all' || $type === 'series') {
            $sQuery = Series::with('genres')->where('is_published', true);
            if ($query) {
                $sQuery->where('title', 'like', "%{$query}%");
            }
            if ($genreId) {
                $sQuery->whereHas('genres', fn($q) => $q->where('genres.id', $genreId));
            }
            if ($minRating) {
                $sQuery->where('rating', '>=', $minRating);
            }
            $series = $sQuery->get()->map(function ($item) {
                $item->content_type = 'series';
                return $item;
            });
        }

        $results = $movies->concat($series);

        if ($sortBy === 'rating') {
            $results = $results->sortByDesc('rating')->values();
        } elseif ($sortBy === 'newest') {
            $results = $results->sortByDesc('release_date')->values();
        } else {
            $results = $results->sortByDesc('views_count')->values();
        }

        return response()->json([
            'success' => true,
            'data' => $results
        ]);
    }
}
