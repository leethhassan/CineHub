<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Series;
use App\Models\Season;
use App\Models\Episode;
use Illuminate\Http\Request;

class SeriesController extends Controller
{
    public function index(Request $request)
    {
        $series = Series::with(['genres', 'cast'])
            ->where('is_published', true)
            ->orderBy('id', 'desc')
            ->paginate(20);

        return response()->json([
            'success' => true,
            'data' => $series
        ]);
    }

    public function show($id)
    {
        $series = Series::with(['genres', 'cast', 'seasons.episodes'])
            ->findOrFail($id);
        $series->increment('views_count');

        return response()->json([
            'success' => true,
            'data' => $series
        ]);
    }

    public function seasons($id)
    {
        $seasons = Season::with('episodes')
            ->where('series_id', $id)
            ->orderBy('season_number', 'asc')
            ->get();

        return response()->json([
            'success' => true,
            'data' => $seasons
        ]);
    }

    public function episodes($seasonId)
    {
        $episodes = Episode::where('season_id', $seasonId)
            ->orderBy('episode_number', 'asc')
            ->get();

        return response()->json([
            'success' => true,
            'data' => $episodes
        ]);
    }
}
