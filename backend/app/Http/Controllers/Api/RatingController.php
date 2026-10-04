<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Rating;
use App\Models\Movie;
use App\Models\Series;
use Illuminate\Http\Request;

class RatingController extends Controller
{
    public function store(Request $request)
    {
        $validated = $request->validate([
            'content_id' => 'required|integer',
            'content_type' => 'required|in:movie,series',
            'rating' => 'required|integer|min:1|max:5',
            'review' => 'nullable|string|max:1000',
        ]);

        $rating = Rating::updateOrCreate(
            [
                'user_id' => $request->user()->id,
                'content_id' => $validated['content_id'],
                'content_type' => $validated['content_type'],
            ],
            [
                'rating' => $validated['rating'],
                'review' => $validated['review'] ?? '',
            ]
        );

        // Recalculate average rating on parent content
        $avg = Rating::where('content_id', $validated['content_id'])
            ->where('content_type', $validated['content_type'])
            ->avg('rating');

        if ($validated['content_type'] === 'movie') {
            Movie::where('id', $validated['content_id'])->update(['rating' => round($avg, 1)]);
        } else {
            Series::where('id', $validated['content_id'])->update(['rating' => round($avg, 1)]);
        }

        return response()->json([
            'success' => true,
            'message' => 'Rating submitted successfully',
            'data' => $rating
        ]);
    }

    public function destroy(Request $request, $id)
    {
        Rating::where('user_id', $request->user()->id)
            ->where('id', $id)
            ->delete();

        return response()->json([
            'success' => true,
            'message' => 'Rating removed'
        ]);
    }
}
