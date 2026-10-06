<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Watchlist;
use App\Models\Movie;
use App\Models\Series;
use Illuminate\Http\Request;

class WatchlistController extends Controller
{
    public function index(Request $request)
    {
        $userId = $request->user()->id;
        $items = Watchlist::where('user_id', $userId)->orderBy('created_at', 'desc')->get();

        $enriched = $items->map(function ($item) {
            $content = $item->content_type === 'movie'
                ? Movie::find($item->content_id)
                : Series::find($item->content_id);
            return [
                'id' => $item->id,
                'content_id' => $item->content_id,
                'content_type' => $item->content_type,
                'title' => $content?->title ?? '',
                'poster' => $content?->poster ?? '',
                'backdrop' => $content?->backdrop ?? '',
                'rating' => $content?->rating ?? 0.0,
                'release_year' => $content?->release_date ?? '',
                'created_at' => $item->created_at,
            ];
        })->filter(fn($i) => !empty($i['title']))->values();

        return response()->json([
            'success' => true,
            'data' => $enriched
        ]);
    }

    public function store(Request $request)
    {
        $request->validate([
            'content_id' => 'required|integer',
            'content_type' => 'required|in:movie,series'
        ]);

        $item = Watchlist::firstOrCreate([
            'user_id' => $request->user()->id,
            'content_id' => $request->content_id,
            'content_type' => $request->content_type,
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Added to watchlist',
            'data' => $item
        ], 201);
    }

    public function destroy(Request $request, $id)
    {
        Watchlist::where('user_id', $request->user()->id)
            ->where(function ($q) use ($id) {
                $q->where('id', $id)->orWhere('content_id', $id);
            })
            ->delete();

        return response()->json([
            'success' => true,
            'message' => 'Removed from watchlist'
        ]);
    }
}
