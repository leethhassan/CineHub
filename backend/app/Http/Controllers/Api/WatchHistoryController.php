<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\WatchHistory;
use App\Models\Movie;
use App\Models\Series;
use App\Models\Episode;
use Illuminate\Http\Request;

class WatchHistoryController extends Controller
{
    public function index(Request $request)
    {
        $userId = $request->user()->id;
        $histories = WatchHistory::where('user_id', $userId)
            ->orderBy('updated_at', 'desc')
            ->get();

        $enriched = $histories->map(function ($h) {
            $title = '';
            $subtitle = '';
            $poster = '';
            $backdrop = '';
            $videoUrl = '';

            if ($h->content_type === 'movie') {
                $m = Movie::find($h->content_id);
                if ($m) {
                    $title = $m->title;
                    $subtitle = "{$m->duration_minutes} min";
                    $poster = $m->poster;
                    $backdrop = $m->backdrop;
                    $videoUrl = $m->video_url;
                }
            } else {
                $s = Series::find($h->content_id);
                $ep = $h->episode_id ? Episode::find($h->episode_id) : null;
                if ($s) {
                    $title = $s->title;
                    $subtitle = $ep ? $ep->title : 'Episode 1';
                    $poster = $s->poster;
                    $backdrop = $s->backdrop;
                    $videoUrl = $ep ? $ep->video_url : $s->trailer_url;
                }
            }

            $pct = $h->duration_seconds > 0 ? ($h->progress_seconds / $h->duration_seconds) : 0;

            return [
                'id' => $h->id,
                'content_id' => $h->content_id,
                'content_type' => $h->content_type,
                'episode_id' => $h->episode_id,
                'title' => $title,
                'subtitle' => $subtitle,
                'poster' => $poster,
                'backdrop' => $backdrop,
                'video_url' => $videoUrl,
                'progress_seconds' => $h->progress_seconds,
                'duration_seconds' => $h->duration_seconds,
                'progress_percentage' => min(1.0, max(0.0, $pct)),
                'updated_at' => $h->updated_at,
            ];
        })->filter(fn($h) => !empty($h['title']))->values();

        return response()->json([
            'success' => true,
            'data' => $enriched
        ]);
    }

    public function saveProgress(Request $request)
    {
        $validated = $request->validate([
            'content_id' => 'required|integer',
            'content_type' => 'required|in:movie,series',
            'episode_id' => 'nullable|integer',
            'progress_seconds' => 'required|integer|min:0',
            'duration_seconds' => 'required|integer|min:0',
        ]);

        $record = WatchHistory::updateOrCreate(
            [
                'user_id' => $request->user()->id,
                'content_id' => $validated['content_id'],
                'content_type' => $validated['content_type'],
            ],
            [
                'episode_id' => $validated['episode_id'] ?? null,
                'progress_seconds' => $validated['progress_seconds'],
                'duration_seconds' => $validated['duration_seconds'],
            ]
        );

        return response()->json([
            'success' => true,
            'message' => 'Progress saved successfully',
            'data' => $record
        ]);
    }

    public function removeProgress(Request $request, $contentId)
    {
        WatchHistory::where('user_id', $request->user()->id)
            ->where('content_id', $contentId)
            ->delete();

        return response()->json([
            'success' => true,
            'message' => 'Progress cleared'
        ]);
    }
}
